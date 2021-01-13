package com.gft.algamoney.apiestudo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gft.algamoney.apiestudo.model.Lancamento;
import com.gft.algamoney.apiestudo.model.Pessoa;
import com.gft.algamoney.apiestudo.repository.LancamentoRepository;
import com.gft.algamoney.apiestudo.repository.PessoaRepository;
import com.gft.algamoney.apiestudo.service.exception.PessoaInexistenteOuInativaException;

@Service
public class LancamentoService {
	
	@Autowired
	private PessoaRepository pessoaRepository;
	
	@Autowired
	private LancamentoRepository lancamentoRepository;
	

	public Lancamento salvar(Lancamento lancamento) {
		Pessoa pessoa = pessoaRepository.getOne(lancamento.getPessoa().getCodigo());
		if(pessoa == null || pessoa.isInativo()){
			throw new PessoaInexistenteOuInativaException();
		}
		return lancamentoRepository.save(lancamento);
	}

}
