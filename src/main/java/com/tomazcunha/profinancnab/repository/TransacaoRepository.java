package com.tomazcunha.profinancnab.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.tomazcunha.profinancnab.entity.Transacao;

// Precisamos da dependência "Spring Data JDBC"
public interface TransacaoRepository extends CrudRepository<Transacao, Long> {

    // Incluir anotações Spring Data nos campos do Transacao.
    // Injetar TransacaoRepository no construtor de TransacaoRepository.


    // Só precisamos definir a interface e assinatura do método para criar a consulta com o Spring Data.
    // Select * from transacao order by nome_loja asc, id desc
    // Essa vai ser a consulta montada pelo Spring Data.
    List<Transacao> findAllByOrderByNomeDaLojaAscIdDesc();

}
