package com.tomazcunha.profinancnab.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tomazcunha.profinancnab.entity.Transacao;
import com.tomazcunha.profinancnab.entity.TransacaoReport;
import com.tomazcunha.profinancnab.repository.TransacaoRepository;

// Criando a regra de negócio para transações
@Service
public class TransacaoService {

    private TransacaoRepository repository;

    public TransacaoService(TransacaoRepository repository){
        this.repository = repository;
    }

    // Mudando para findByOrderByNomeDaLojaAscIdDesc.
    // public List<TransacaoReport> listTotaisTransacoesPorNomeDaLoja() {
    public List<Transacao> listTotaisTransacoesPorNomeDaLoja() {
        // Para trazer os valores, precisamos dos dados do banco. Precisamos criar os repositórios.
        // var transacoes = repository.findAll();
        var transacoes = repository.findAllByOrderByNomeDaLojaAscIdDesc();
        return transacoes;
    }

    // Testando a consulta no banco
    public Iterable<Transacao> IterableTotaisTransacoesPorNomeDaLoja() {
        var transacoes = repository.findAll();
        return transacoes;
    }







}
