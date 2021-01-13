package com.gft.algamoney.apiestudo.controller;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.gft.algamoney.apiestudo.event.RecursoCriadoEvent;
import com.gft.algamoney.apiestudo.model.Pessoa;
import com.gft.algamoney.apiestudo.repository.PessoaRepository;
import com.gft.algamoney.apiestudo.service.PessoaService;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/pessoas")
public class PessoaControler {
	
	@Autowired
	public PessoaRepository pessoaRepository;
	
	@Autowired
	private PessoaService pessoaService;
	
	@Autowired
	private ApplicationEventPublisher publisher;
	
    @ApiImplicitParam(name = "Authorization",
    		value = "Bearer Token", 
    		required = true, 
    		allowEmptyValue = false, 
    		paramType = "header", 
    		example = "Bearer access_token")
    @ApiOperation("Listar Pessoas")
	@GetMapping
	public ResponseEntity <?> listar() {
		List<Pessoa> pessoas = pessoaRepository.findAll();
		return !pessoas.isEmpty() ? ResponseEntity.ok(pessoas) : ResponseEntity.notFound().build();
	}
	
    @ApiImplicitParam(name = "Authorization",
    		value = "Bearer Token", 
    		required = true, 
    		allowEmptyValue = false, 
    		paramType = "header", 
    		example = "Bearer access_token")
    @ApiOperation("Cadastrar pessoa")
	@PostMapping
	public ResponseEntity<Pessoa> criar(@Validated @RequestBody Pessoa pessoa, HttpServletResponse response) {
		Pessoa pessoaSalva =  pessoaRepository.save(pessoa);
		
		publisher.publishEvent(new RecursoCriadoEvent(this, response, pessoaSalva.getCodigo()));
		
		return ResponseEntity.status(HttpStatus.CREATED).body(pessoaSalva);				
		
	}
	
    
    @ApiImplicitParam(name = "Authorization",
    		value = "Bearer Token", 
    		required = true, 
    		allowEmptyValue = false, 
    		paramType = "header", 
    		example = "Bearer access_token")
    @ApiOperation("Buscar pessoa pelo código")
	@GetMapping("/{codigo}")
	public ResponseEntity<Pessoa> buscarPeloCodigo (@PathVariable Long codigo){
		Pessoa pessoa = pessoaRepository.findById(codigo).orElse(null);		
		return pessoa != null ? ResponseEntity.ok(pessoa) : ResponseEntity.notFound().build(); 
	
	}
	
    @ApiImplicitParam(name = "Authorization",
    		value = "Bearer Token", 
    		required = true, 
    		allowEmptyValue = false, 
    		paramType = "header", 
    		example = "Bearer access_token")
    @ApiOperation("Remover pessoa da lista")
	@DeleteMapping("/{codigo}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void remover(@PathVariable Long codigo) {
		this.pessoaRepository.deleteById(codigo);
	}
	
	
	@PutMapping("/{codigo}")
	public ResponseEntity<Pessoa> atualizar(@PathVariable Long codigo, @Validated @RequestBody Pessoa pessoa){
		Pessoa pessoaSalva = pessoaService.atualizar(codigo, pessoa);
		return ResponseEntity.ok(pessoaSalva);
	}
	
	@PutMapping("/{codigo}/ativo")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void atualizarPropriedadeAtivo(@PathVariable Long codigo, @RequestBody Boolean ativo) {
		pessoaService.atualizarPropriedadeAtivo(codigo, ativo);
	}


	

}
