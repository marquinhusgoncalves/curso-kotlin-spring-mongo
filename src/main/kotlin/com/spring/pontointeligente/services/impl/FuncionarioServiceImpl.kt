package com.spring.pontointeligente.services.impl

import com.spring.pontointeligente.documents.Funcionario
import com.spring.pontointeligente.repositories.FuncionarioRepository
import com.spring.pontointeligente.services.FuncionarioService
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class FuncionarioServiceImpl(val funcionarioRepository: FuncionarioRepository) : FuncionarioService {

    private val logger = LoggerFactory.getLogger(FuncionarioServiceImpl::class.java)

    override fun persistir(funcionario: Funcionario): Funcionario
            = funcionarioRepository.save(funcionario)

    override fun buscarPorCpf(cpf: String): Funcionario? {
        var funcionario: Funcionario? = null
        try {
            funcionario = funcionarioRepository.findByCpf(cpf)
        }catch (e: Exception){
            logger.info("Funcionario não encontrado pelo cpf informado.")
        }

        return funcionario
    }

    override fun buscarPorEmail(email: String): Funcionario? {
        var funcionario: Funcionario? = null
        try {
            funcionario = funcionarioRepository.findByEmail(email)
        }catch (e: Exception){
            logger.info("Funcionário não encontrado pelo email informado.")
        }
        return funcionario
    }

    override fun buscarPorId(id: String): Funcionario? = funcionarioRepository.findByIdOrNull(id)
}