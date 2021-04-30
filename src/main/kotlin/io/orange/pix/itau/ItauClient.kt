package io.orange.pix.itau

import io.micronaut.http.annotation.Get
import io.micronaut.http.client.annotation.Client


@Client("http://localhost:9091/api/v1/clientes/")
interface ItauClient {

    @Get("{clienteId}")
    fun findById(clienteId: String): ItauResponse?
}