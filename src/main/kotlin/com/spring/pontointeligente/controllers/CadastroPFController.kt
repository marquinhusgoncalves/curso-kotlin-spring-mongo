package com.spring.pontointeligente.controllers

import com.spring.pontointeligente.documents.Empresa
import com.spring.pontointeligente.documents.Funcionario
import com.spring.pontointeligente.dto.CadastroPFDto
import com.spring.pontointeligente.enums.PerfilEnum
import com.spring.pontointeligente.response.Response
import com.spring.pontointeligente.services.EmpresaService
import com.spring.pontointeligente.services.FuncionarioService
import com.spring.pontointeligente.utils.SenhaUtils
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.validation.ObjectError
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/api/cadastrar-pf")
class CadastroPFController(val empresaService: EmpresaService,
                           val funcionarioService: FuncionarioService) {

    @PostMapping
    fun cadastrar(@Valid @RequestBody cadastroPFDto: CadastroPFDto,
                  result: BindingResult): ResponseEntity<Response<CadastroPFDto>> {
        val response: Response<CadastroPFDto> = Response<CadastroPFDto>()

        val empresa: Empresa? = empresaService.buscarPorCnpj(cadastroPFDto.cnpj)
        validarDadosExistentes(cadastroPFDto, empresa, result)

        if (result.hasErrors()) {
            for (erro in result.allErrors) response.erros.add(erro.defaultMessage!!)
            return ResponseEntity.badRequest().body(response)
        }

        val funcionario: Funcionario = converterDtoParaFuncionario(cadastroPFDto, empresa!!)

        funcionarioService.persistir(funcionario)
        response.data = converterCadastroPFDto(funcionario, empresa!!)

        return ResponseEntity.ok(response)
    }

    private fun validarDadosExistentes(cadastroPFDto: CadastroPFDto, empresa: Empresa?,
                                       result: BindingResult) {
        if (empresa == null) {
            result.addError(ObjectError("empresa", "Empresa não cadastrada."))
        }

        val funcionarioCpf: Funcionario? = funcionarioService.buscarPorCpf(cadastroPFDto.cpf)
        if (funcionarioCpf != null) {
            result.addError(ObjectError("funcionario", "CPF já existente."))
        }

        val funcionarioEmail: Funcionario? = funcionarioService.buscarPorEmail(cadastroPFDto.email)
        if (funcionarioEmail != null) {
            result.addError(ObjectError("funcionario", "Email já existente."))
        }
    }

    private fun converterDtoParaFuncionario(cadastroPFDto: CadastroPFDto, empresa: Empresa) =
            Funcionario(cadastroPFDto.nome, cadastroPFDto.email,
                    SenhaUtils().gerarBcrypt(cadastroPFDto.senha.toString()), cadastroPFDto.cpf,
                    PerfilEnum.ROLE_USUARIO, empresa.id.toString(),
                    cadastroPFDto.valorHora?.toDouble(), cadastroPFDto.qtdHorasTrabalhoDia?.toFloat(),
                    cadastroPFDto.qtdHorasAlmoco?.toFloat(), cadastroPFDto.id)


    private fun converterCadastroPFDto(funcionario: Funcionario, empresa: Empresa): CadastroPFDto  =
            CadastroPFDto(funcionario.nome, funcionario.email, "", funcionario.cpf,
                    empresa.cnpj, empresa.id.toString(),funcionario.valorHora.toString(),
                    funcionario.qtdHorasTrabalhoDia.toString(),
                    funcionario.qtdHorasTrabalhoDia.toString(),
                    funcionario.id)
}
