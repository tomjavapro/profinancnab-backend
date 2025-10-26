package com.tomazcunha.profinancnab.entity;

import java.math.BigDecimal;
import java.util.List;

// Gerando um relatório agrupado por nome da loja.
public record TransacaoReport(
    String nomeDaLoja,
    BigDecimal toal,
    List<Transacao> tansacoes) {

}
