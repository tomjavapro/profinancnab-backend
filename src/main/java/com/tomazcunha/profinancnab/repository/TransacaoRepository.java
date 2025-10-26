package com.tomazcunha.profinancnab.repository;

import org.springframework.data.repository.CrudRepository;

import com.tomazcunha.profinancnab.entity.Transacao;

// Precisamos da dependência "Spring Data JDBC"
public interface TransacaoRepository extends CrudRepository<Transacao, Long> {

    // Incluir anotações Spring Data nos campos do Transacao.
    // Injetar TransacaoRepository no construtor de TransacaoRepository.


}
