package com.spring.pontointeligente.controllers

import com.spring.pontointeligente.documents.Funcionario
import com.spring.pontointeligente.documents.Lancamento
import com.spring.pontointeligente.dto.LancamentoDto
import com.spring.pontointeligente.enums.TipoEnum
import com.spring.pontointeligente.response.Response
import com.spring.pontointeligente.services.FuncionarioService
import com.spring.pontointeligente.services.LancamentoService
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.validation.ObjectError
import org.springframework.web.bind.annotation.*
import java.text.SimpleDateFormat
import java.util.*
import javax.validation.Valid

@RestController
@RequestMapping("/api/lancamentos")
class LancamentoController (val lancamentoService: LancamentoService, val funcionarioService: FuncionarioService){

    @Value("\${paginacao.qtd_por_pagina}")
    val qtdPagina: Int = 15

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    @PostMapping
    fun adicionar(@Valid @RequestBody lancamentoDto: LancamentoDto, result: BindingResult): ResponseEntity<Response<LancamentoDto>> {
        val response: Response<LancamentoDto> = Response<LancamentoDto>()
        validarFuncionario(lancamentoDto, result)

        if (result.hasErrors()){
            for (erro in result.allErrors) response.erros.add(erro.defaultMessage.toString())
            return ResponseEntity.badRequest().body(response)
        }

        val lancamento: Lancamento = converterDtoParaLancamento(lancamentoDto,result)
        lancamentoService.persistir(lancamento)
        response.data = converterLancamentoParaDto(lancamento)
        return ResponseEntity.ok(response)
    }

    @GetMapping(value = ["/{id}"])
    fun listarPorId(@PathVariable ("id") id: String): ResponseEntity<Response<LancamentoDto>> {
        val response: Response<LancamentoDto> = Response<LancamentoDto>()
        val lancamento: Lancamento? = lancamentoService.buscarPorId(id)

        if (lancamento == null) {
            response.erros.add("Lançamento não encontrado para o Id: $id")
            return ResponseEntity.badRequest().body(response)
        }

        response.data = converterLancamentoParaDto(lancamento)
        return ResponseEntity.ok(response)
    }

    @GetMapping(value = ["/funcionario/{funcionarioId}"])
    fun listarPorFuncionarioId(@PathVariable ("funcionarioId") funcionarioId: String,
                               @RequestParam (value = "pag", defaultValue = "0") pag: Int,
                               @RequestParam (value = "ord", defaultValue = "id") ord: String,
                               @RequestParam (value = "dir", defaultValue = "DESC") dir: String):
            ResponseEntity<Response<Page<LancamentoDto>>> {

        val response: Response<Page<LancamentoDto>> = Response<Page<LancamentoDto>>()
        val pageRequest: PageRequest = PageRequest.of(pag, qtdPagina, Sort.Direction.valueOf(dir), ord)
        val lancamentos: Page<Lancamento> = lancamentoService.buscarPorFuncionarioId(funcionarioId, pageRequest)

        val lancamentosDto: Page<LancamentoDto> = lancamentos.map { lancamento: Lancamento -> converterLancamentoParaDto(lancamento) }

        response.data = lancamentosDto
        return ResponseEntity.ok(response)
    }

//    @PutMapping
//    fun atualizar(@PathVariable("id") id: String, @Valid @RequestBody lancamentoDto: LancamentoDto,
//                  result: BindingResult): ResponseEntity<Response<LancamentoDto>> {
//        val response: Response<LancamentoDto> = Response<LancamentoDto>()
//        validarFuncionario(lancamentoDto, result)
//        lancamentoDto.id = id
//        val lancamento: Lancamento = converterDtoParaLancamento(lancamentoDto, result)
//
//        if (result.hasErrors()){
//            for (erro in result.allErrors) response.erros.add(erro.defaultMessage!!)
//            return ResponseEntity.badRequest().body(response)
//        }
//
//        lancamentoService.persistir(lancamento)
//        response.data = converterLancamentoParaDto(lancamento)
//        return ResponseEntity.ok(response)
//    }

//    @DeleteMapping(value = ["/{id}"])
//    fun remover(@PathVariable("id") id: String): ResponseEntity<Response<String>> {
//        val response: Response<String> = Response<String>()
//        val lancamento: Lancamento? = lancamentoService.buscarPorId(id).orElse(null)
//
//        if (lancamento == null) {
//            response.erros.add("Erro ao remover lançamento. Registro não encontrado para o Id: $id")
//            return ResponseEntity.badRequest().body(response)
//        }
//
//        lancamentoService.remover(id)
//        return ResponseEntity.ok(Response<String>())
//
//    }

    private fun validarFuncionario(lancamentoDto: LancamentoDto, result: BindingResult){
        if(lancamentoDto.funcionarioId == null){
            result.addError(ObjectError("funcionario","Funcionário não informado."))
            return
        }

        val funcionario: Funcionario? = funcionarioService.buscarPorId(lancamentoDto.funcionarioId)
        if(funcionario == null){
            result.addError(ObjectError("funcionario", "Funcionário não encontrado. Id inexistente."))
        }
    }

    private fun converterLancamentoParaDto(lancamento: Lancamento): LancamentoDto =
            LancamentoDto(dateFormat.format(lancamento.data), lancamento.tipo.toString(), lancamento.descricao, lancamento.localizacao, lancamento.funcionarioId, lancamento.id)

    private fun converterDtoParaLancamento(lancamentoDto: LancamentoDto, result: BindingResult): Lancamento {
        if (lancamentoDto.id != null) {
            val lanc: Lancamento? = lancamentoService.buscarPorId(lancamentoDto.id!!)
            if (lanc == null) result.addError(ObjectError("lancamento", "Lançamento não encontrado."))
        }

        return Lancamento(dateFormat.parse(lancamentoDto.data), TipoEnum.valueOf(lancamentoDto.tipo!!), lancamentoDto.funcionarioId!!, lancamentoDto.descricao, lancamentoDto.id)
    }

}