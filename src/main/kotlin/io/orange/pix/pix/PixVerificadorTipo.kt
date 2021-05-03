package io.orange.pix.pix

import java.util.*
import javax.validation.constraints.Pattern

class PixVerificadorTipo {

    fun verificaTipoPix(typePix: Int, keyPix: String): Boolean{
         return when(typePix){
            0 -> keyPix.matches("^[0-9]{11}\$".toRegex())
            1 -> keyPix.matches("^\\+[1-9][0-9]\\d{1,14}\$".toRegex())
            2 -> keyPix.matches("^[a-z0-9.]+@[a-z0-9]+\\.[a-z]+\\.([a-z]+)?\$".toRegex())
            3 -> true
            else -> false
        }
    }
}