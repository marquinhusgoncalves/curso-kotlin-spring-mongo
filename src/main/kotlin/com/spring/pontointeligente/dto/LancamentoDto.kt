package com.spring.pontointeligente.dto

import javax.validation.constraints.NotEmpty

data class LancamentoDto (
        @get:NotEmpty(message = "Data não pode ser vazia.")
        val data: String = "",

        @get:NotEmpty(message = "Tipo não pode ser vazio.")
        val tipo: String = "",

        val descricao: String? = null,
        val localizao: String? = null,
        val funcionarioId: String? = null,
        var id: String? = null
)