package com.tomazcunha.profinancnab.entity;

import java.math.BigDecimal;

public enum TipoTransacao {

    DEBITO(1),
    BOLETO(2),
    FINANCIAMENTO(3),
    CREDITO(4),
    RECEBIMENTO_EMPRESTIMO(5),
    VENDAS(6),
    RECEBIMENTO_TEC(7),
    RECEBIMENTO_DOC(8),
    ALUGUEL(9);
    // Tipos mapeados de acordo com a especificação do problema.

    private int tipo; // Será o valor numérico associado.

    // Dependendo o tipo que for passado, teremos um enum sendo criado.
    private TipoTransacao(int tipo) {
        this.tipo = tipo;
    }

    // Design Strategy (Adaptado para enuns)
    // Baseado no tipo da transação, vai responder se o valor é positivo ou negativo.
    // public int getSinal() {
    public BigDecimal getSinal() {
        return switch (tipo) {
            // case 1, 4, 5, 6, 7, 8 -> 1; // créditos
            // case 2, 3, 9 -> -1; // débitos
            // default -> 0;

            // Corrigindo
            // multiply só pode ser feita com BigDecimal
            case 4, 5, 6, 7, 8 -> new BigDecimal(1); // créditos
            case 1, 2, 3, 9 -> new BigDecimal(-1); // débitos
            default -> new BigDecimal(0);
        };
    }

    // Nos ENUNS só depodemos ter construtores privados, precisamos ter uma forma de criar(o objeto), baseado no tipo, o enum correspondente.
    public static TipoTransacao findByTipo(int tipo) {
        for (TipoTransacao tipoTransacao : values()) {
            if (tipoTransacao.tipo == tipo) {
                return tipoTransacao;
            }
        }
        throw new IllegalArgumentException("Invalid tipo: " + tipo); // Caso receba algum tipo inválido.
    }

}
