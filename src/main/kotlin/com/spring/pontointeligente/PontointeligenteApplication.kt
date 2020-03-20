package com.spring.pontointeligente

import com.spring.pontointeligente.documents.Empresa
import com.spring.pontointeligente.documents.Funcionario
import com.spring.pontointeligente.documents.Lancamento
import com.spring.pontointeligente.enums.PerfilEnum
import com.spring.pontointeligente.enums.TipoEnum
import com.spring.pontointeligente.repositories.EmpresaRepository
import com.spring.pontointeligente.repositories.FuncionarioRepository
import com.spring.pontointeligente.repositories.LancamentoRepository
import com.spring.pontointeligente.utils.SenhaUtils
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.runApplication
import java.time.Instant
import java.util.*

@SpringBootApplication(exclude = arrayOf(SecurityAutoConfiguration::class))
class PontointeligenteApplication(val empresaRepository: EmpresaRepository,
								  val funcionarioRepository: FuncionarioRepository,
								  val lancamentoRepository: LancamentoRepository) : CommandLineRunner {

	override fun run(vararg args: String?) {
		empresaRepository.deleteAll()
		funcionarioRepository.deleteAll()
		lancamentoRepository.deleteAll()

		var empresa: Empresa = Empresa("Empresa LTDA", "35417492000108")
		empresa = empresaRepository.save(empresa)

		var admin: Funcionario
				= Funcionario("Admin", "admin@empresa.com",
				SenhaUtils().gerarBcrypt("123456"), "42357696001",
				PerfilEnum.ROLE_ADMIN, empresa.id!!)
		admin = funcionarioRepository.save(admin)

		var funcionario: Funcionario
				= Funcionario("Funcionario", "funcionario@empresa.com",
				SenhaUtils().gerarBcrypt("654321"), "17347006023",
				PerfilEnum.ROLE_USUARIO, empresa.id!!)
		funcionario = funcionarioRepository.save(funcionario)

		var lancamentoFuncionario: Lancamento
				= Lancamento(Date.from(Instant.now()), TipoEnum.INICIO_TRABALHO, funcionario.id!!,
				"teste", "testelocal")
		lancamentoFuncionario = lancamentoRepository.save(lancamentoFuncionario)

		println("Empresa ID: " + empresa.id)
		println("Admin ID: " + admin.id)
		println("Funcionario ID: " + funcionario.id)
		println("Lancamento ID: " + lancamentoFuncionario.id)
	}
}

fun main(args: Array<String>) {
	runApplication<PontointeligenteApplication>(*args)
}
