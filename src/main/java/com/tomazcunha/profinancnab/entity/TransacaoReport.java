package com.tomazcunha.profinancnab.entity;

import java.math.BigDecimal;
import java.util.List;

// Gerando um relatório agrupado por nome da loja.
public record TransacaoReport(
    String nomeDaLoja,
    BigDecimal total,
    List<Transacao> transacoes) {

    public TransacaoReport addTotal(BigDecimal valor) {
        return new TransacaoReport(nomeDaLoja, total.add(valor), transacoes);
    }

    public TransacaoReport addTransacao(Transacao transacao) {
        transacoes.add(transacao); // Adicionando a transação nova.
        return new TransacaoReport(nomeDaLoja, total, transacoes); //
    }
    // Como TransacaoReport é um objeto imutável, precisamos criar objetos novoa a partir do objeto original para incluir as modificações.

}
