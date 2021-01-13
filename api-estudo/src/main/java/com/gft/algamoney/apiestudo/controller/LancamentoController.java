package com.gft.algamoney.apiestudo.controller;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.gft.algamoney.apiestudo.event.RecursoCriadoEvent;
import com.gft.algamoney.apiestudo.exceptionhandler.AlgamoneyExceptionHandler.Erro;
import com.gft.algamoney.apiestudo.model.Lancamento;
import com.gft.algamoney.apiestudo.repository.LancamentoRepository;
import com.gft.algamoney.apiestudo.repository.filter.LancamentoFilter;
import com.gft.algamoney.apiestudo.service.LancamentoService;
import com.gft.algamoney.apiestudo.service.exception.PessoaInexistenteOuInativaException;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/lancamentos")
public class LancamentoController {

	@Autowired
	public LancamentoRepository lancamentoRepository;
	
	@Autowired
	private LancamentoService lancamentoService;
	
	@Autowired
	private ApplicationEventPublisher publisher;
	
	@Autowired
	private MessageSource messageSource;
	
	@GetMapping
	public Page<Lancamento> pesquisar(LancamentoFilter lancamentoFilter, Pageable pageable){
		return lancamentoRepository.filtrar(lancamentoFilter, pageable);
		}
	
	@ApiImplicitParam(name = "Authorization",
    		value = "Bearer Token", 
    		required = true, 
    		allowEmptyValue = false, 
    		paramType = "header", 
    		example = "Bearer access_token")
    @ApiOperation("Buscar Lançamento pelo código")
	@GetMapping("/{codigo}")
	public ResponseEntity<Lancamento> buscarPeloCodigo(@PathVariable Long codigo){
		Lancamento lancamento = lancamentoRepository.findById(codigo).orElse(null);
		return lancamento != null ? ResponseEntity.ok(lancamento) : ResponseEntity.notFound().build(); 
 
	}
	
	
	@ApiImplicitParam(name = "Authorization",
    		value = "Bearer Token", 
    		required = true, 
    		allowEmptyValue = false, 
    		paramType = "header", 
    		example = "Bearer access_token")
    @ApiOperation("Cadastrar Lançamento")
	@PostMapping
	public ResponseEntity<Lancamento> criar(@Validated @RequestBody Lancamento lancamento, HttpServletResponse response) {
		Lancamento lancamentoSalvo =  lancamentoService.salvar(lancamento);
		
		publisher.publishEvent(new RecursoCriadoEvent(this, response, lancamentoSalvo.getCodigo()));
		
		return ResponseEntity.status(HttpStatus.CREATED).body(lancamentoSalvo);			
		
	}
	
	@ApiImplicitParam(name = "Authorization",
    		value = "Bearer Token", 
    		required = true, 
    		allowEmptyValue = false, 
    		paramType = "header", 
    		example = "Bearer access_token")
    @ApiOperation("Remover Lançamento")
	@DeleteMapping("/{codigo}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void remover(@PathVariable Long codigo) {
		this.lancamentoRepository.deleteById(codigo);
	}
	
	@ExceptionHandler({ PessoaInexistenteOuInativaException.class })
	public ResponseEntity<Object> handlePessoaInexistenteOuInativaException(PessoaInexistenteOuInativaException ex){
		String mensagemUsuario  = messageSource.getMessage("pessoa.inexistente-ou-inativa" , null, LocaleContextHolder.getLocale());
		String mensagemDesenvolvedor = ex.toString();
		List<Erro> erros = Arrays.asList(new Erro(mensagemUsuario, mensagemDesenvolvedor));
		return ResponseEntity.badRequest().body(erros);
	}
}
