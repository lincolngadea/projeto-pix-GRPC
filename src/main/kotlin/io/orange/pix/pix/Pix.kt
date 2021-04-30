package io.orange.pix.pix

import io.orange.pix.AccountType
import io.orange.pix.KeyType
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.validation.constraints.NotBlank

@Entity
class Pix(
    @field:NotBlank
    val idClient: String,
    @field:NotBlank
    val keyType: KeyType,
    @field:NotBlank
    val keyPix: String,
    @field:NotBlank
    val account: AccountType
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var ID: Long? = null
}
