package com.gft.algamoney.apiestudo.controller;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gft.algamoney.apiestudo.event.RecursoCriadoEvent;
import com.gft.algamoney.apiestudo.model.Categoria;
import com.gft.algamoney.apiestudo.repository.CategoriaRepository;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@Api(tags = "Categoria")
@RestController
@RequestMapping("/categorias")
public class CategoriaController {
	
	@Autowired
	public CategoriaRepository categoriaRepository;
	
	@Autowired
	private ApplicationEventPublisher publisher;
	
	
	@ApiImplicitParam(name = "Authorization",
    		value = "Bearer Token", 
    		required = true, 
    		allowEmptyValue = false, 
    		paramType = "header", 
    		example = "Bearer access_token")
    @ApiOperation("Listar Categorias")
	@GetMapping
	public ResponseEntity <?> listar() {
		List<Categoria> categorias = categoriaRepository.findAll();
		return !categorias.isEmpty() ? ResponseEntity.ok(categorias) : ResponseEntity.notFound().build();
	}
	
	
	@ApiImplicitParam(name = "Authorization",
    		value = "Bearer Token", 
    		required = true, 
    		allowEmptyValue = false, 
    		paramType = "header", 
    		example = "Bearer access_token")
    @ApiOperation("Cadastrar Categorias")
	@PostMapping
	public ResponseEntity<Categoria> criar(@Validated @RequestBody Categoria categoria, HttpServletResponse response) {
		Categoria categoriaSalva =  categoriaRepository.save(categoria);		
		publisher.publishEvent(new RecursoCriadoEvent(this, response, categoriaSalva.getCodigo()));		
		return ResponseEntity.status(HttpStatus.CREATED).body(categoriaSalva);
	}
	
	@ApiImplicitParam(name = "Authorization",
    		value = "Bearer Token", 
    		required = true, 
    		allowEmptyValue = false, 
    		paramType = "header", 
    		example = "Bearer access_token")
    @ApiOperation("Buscar Categoria pelo código")
	@GetMapping("/{codigo}")
	public Categoria buscarPeloCodigo (@PathVariable Long codigo){
	return this.categoriaRepository.findById(codigo).orElse(null); 
	
	}
	

}
