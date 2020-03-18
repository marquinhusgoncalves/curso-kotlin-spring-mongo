package com.spring.pontointeligente.services.impl

import com.spring.pontointeligente.documents.Empresa
import com.spring.pontointeligente.repositories.EmpresaRepository
import com.spring.pontointeligente.services.EmpresaService
import org.springframework.stereotype.Service

@Service
class EmpresaServiceImpl(val empresaRepository: EmpresaRepository) : EmpresaService {
    override fun buscarPorCnpj(cnpj: String): Empresa? = empresaRepository.findByCnpj(cnpj)

    override fun persistir(cnpj: Empresa): Empresa = empresaRepository.save(empresa)
}