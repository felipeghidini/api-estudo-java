package com.gft.algamoney.apiestudo.repository.lancamento;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.gft.algamoney.apiestudo.model.Lancamento;
import com.gft.algamoney.apiestudo.repository.filter.LancamentoFilter;

public interface LancamentoRepositoryQuery {
	
	public Page<Lancamento> filtrar(LancamentoFilter lancamentoFilter, Pageable pageable);

}
