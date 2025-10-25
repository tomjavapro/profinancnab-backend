package com.tomazcunha.profinancnab.domain;

import java.math.BigDecimal;

// Esses dados serão preenchidos com base na leitura do arquivo CNAB.
// Esse formato tem correspondência com o arquivo mas vou salvar a transação em formato diferente, no formato de banco. Transasao.class
public record TransacaoCNAB(
    Integer tipo,
    String data,
    BigDecimal valor,
    Long cpf,
    String cartao,
    String hora,
    String donoDaLoja,
    String nomeDaLoja) {

}