package com.spring.pontointeligente.services

import com.spring.pontointeligente.documents.Empresa

interface EmpresaService {
    fun buscarPorCnpj(cnpj: String): Empresa?

    fun persistir(cnpj: Empresa): Empresa
}