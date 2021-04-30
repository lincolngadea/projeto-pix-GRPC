package io.orange.pix.pix

import com.google.protobuf.Timestamp
import io.grpc.Status
import io.grpc.stub.StreamObserver
import io.orange.pix.KeyManagerGrpcServiceGrpc
import io.orange.pix.PixRequest
import io.orange.pix.PixResponse
import io.orange.pix.itau.ItauClient
import org.slf4j.LoggerFactory
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PixEndPoint(
    @Inject val itauClient: ItauClient,
    @Inject val repository: PixRepository
): KeyManagerGrpcServiceGrpc.KeyManagerGrpcServiceImplBase() {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun register(request: PixRequest, responseObserver: StreamObserver<PixResponse>) {

        val pix = Pix(
            idClient = request.idClient,
            keyType = request.keyType,
            keyPix = request.keyPix,
            account = request.accounType
        )

        val findById=itauClient.findById(request.idClient)


        if (repository.existsByIdClient(request.idClient)) {
            responseObserver.onError(
                Status.ALREADY_EXISTS
                .withDescription("id do cliente já existe")
                .asRuntimeException())
            return
        }

       logger.info(findById?.id)

        repository.save(pix)

        val instant: Instant = LocalDateTime.now().atZone(ZoneId.of("UTC")).toInstant()
        val criadoEm = Timestamp.newBuilder()
            .setSeconds(instant.epochSecond)
            .setNanos(instant.nano)
            .build()

        val pixResponse = PixResponse.newBuilder()
            .setPixId(pix.ID!!)
            .setPixKey(pix.idClient)
            .setCreatedAt(criadoEm)
            .build()

        responseObserver.onNext(PixResponse.newBuilder(pixResponse).build())
        responseObserver.onCompleted()

    }
}