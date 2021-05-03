package io.orange.pix.pix

import com.google.protobuf.Timestamp
import io.grpc.Status
import io.grpc.stub.StreamObserver
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.orange.pix.KeyManagerGrpcServiceGrpc
import io.orange.pix.PixRequest
import io.orange.pix.PixResponse
import io.orange.pix.itau.ItauClient
import org.slf4j.LoggerFactory
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PixEndPoint(
    @Inject val itauClient: ItauClient,
    @Inject val repository: PixRepository
) : KeyManagerGrpcServiceGrpc.KeyManagerGrpcServiceImplBase() {

    private val logger = LoggerFactory.getLogger(this::class.java)
    override fun register(request: PixRequest, responseObserver: StreamObserver<PixResponse>) {

        val verificaTipo = PixVerificadorTipo().verificaTipoPix(request.keyType.number, request.keyPix)

//        logger.info(verificaTipo.toString())


        try {
            var keyPixVerificado = request.keyPix
            if(verificaTipo && request.keyType.number == 3){
                keyPixVerificado = UUID.randomUUID().toString()
            }
                val pix = Pix(
                    idClient = request.idClient,
                    keyType = request.keyType,
                    keyPix = keyPixVerificado,
                    account = request.accounType
                )

                val findById = itauClient.findById(request.idClient)

                if (findById == null){
                    responseObserver.onError(
                        Status.ALREADY_EXISTS
                            .withDescription("o id informado não existe")
                            .asRuntimeException()
                    )
                    return
                }

                if(repository.existsByIdClient(request.idClient)){
                    responseObserver.onError(
                        Status.ALREADY_EXISTS
                            .withDescription("id do cliente já existe")
                            .asRuntimeException()
                    )
                    return
                }

                if(!verificaTipo){
                    responseObserver.onError(
                        Status.INVALID_ARGUMENT
                            .withDescription("Chave inválida")
                            .asRuntimeException()
                    )
                    return
                }

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

        } catch (e: HttpClientResponseException) {
            responseObserver.onError(
                Status.INVALID_ARGUMENT
                    .withDescription("erro inesperado!")
                    .asRuntimeException()
            )
            return
        }
    }
}