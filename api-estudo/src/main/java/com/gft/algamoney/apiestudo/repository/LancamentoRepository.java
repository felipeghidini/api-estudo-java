package com.gft.algamoney.apiestudo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gft.algamoney.apiestudo.model.Lancamento;
import com.gft.algamoney.apiestudo.repository.lancamento.LancamentoRepositoryQuery;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long>, LancamentoRepositoryQuery{

}
