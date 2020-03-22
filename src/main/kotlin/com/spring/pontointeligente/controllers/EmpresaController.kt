package com.spring.pontointeligente.controllers

import com.spring.pontointeligente.documents.Empresa
import com.spring.pontointeligente.dto.EmpresaDto
import com.spring.pontointeligente.response.Response
import com.spring.pontointeligente.services.EmpresaService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/empresas")
class EmpresaController(val empresaService: EmpresaService) {

    @GetMapping("/cnpj/{cnpj}")
    fun buscarPorCnpj(@PathVariable("cnpj") cnpj: String): ResponseEntity<Response<EmpresaDto>> {

        val response: Response<EmpresaDto> = Response<EmpresaDto>()
        val empresa: Empresa? = empresaService.buscarPorCnpj(cnpj)

        if(empresa == null){
            response.erros.add("Empresa não encontrada para o CNPJ ${cnpj}")
            return ResponseEntity.badRequest().body(response)
        }

        response.data = converterEmpresaDto(empresa)
        return ResponseEntity.ok().body(response)
    }

    private fun converterEmpresaDto(empresa: Empresa): EmpresaDto = EmpresaDto(empresa.razaoSocial, empresa.cnpj, empresa.id)
}