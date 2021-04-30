package io.orange.pix.pix

import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository

@Repository
interface PixRepository : JpaRepository<Pix, Long>{
        fun existsByIdClient(idClient: String): Boolean
}