package com.tomazcunha.profinancnab;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public record Transacao(
    Long id,
    Integer tipo,
    Date data,
    BigDecimal valor,
    Long cpf,
    String cartao,
    Time hora,
    String donoDaLoja,
    String nomeDaLoja) {

    // Wither Pattern
    // Esse método vai recriar a Transacao mudar apenas a propriedade que eu preciso atualizar. Não é realmente uma atualização, já que record é imutável, é um objeto novo.
    public Transacao withValor(BigDecimal valor){
        return new Transacao(
            this.id(),
            this.tipo(),
            this.data(),
            valor,
            this.cpf(),
            this.cartao(),
            this.hora(),
            this.donoDaLoja(),
            this.nomeDaLoja()

            // Podemos manter dessa forma mais simples, 'valor' continua referente ao parâmetro, enquanto os outros itens ainda serão referentes aos campos.
            // id,
            // tipo,
            // data,
            // valor,
            // cpf,
            // cartao,
            // hora,
            // donoDaLoja,
            // nomeDaLoja
        );
    }

    // Normalizando data
    public Transacao withData(String data) throws ParseException {
        var dateFormat = new SimpleDateFormat("yyyyMMdd");
        var date = dateFormat.parse(data);

        return new Transacao(
            this.id(),
            this.tipo(),
            new Date(date.getTime()),
            this.valor(),
            this.cpf(),
            this.cartao(),
            this.hora(),
            this.donoDaLoja(),
            this.nomeDaLoja()
        );
    }

    // Normalizando hora
    public Transacao withHora(String hora) throws ParseException {
        var dateFormat = new SimpleDateFormat("HHmmss");
        var date = dateFormat.parse(hora);

        return new Transacao(
            this.id(),
            this.tipo(),
            this.data(),
            this.valor(),
            this.cpf(),
            this.cartao(),
            new Time(date.getTime()),
            this.donoDaLoja(),
            this.nomeDaLoja()
        );
    }
}