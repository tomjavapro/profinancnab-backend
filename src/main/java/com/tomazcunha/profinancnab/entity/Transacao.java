package com.tomazcunha.profinancnab.entity;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;

// O editor não atualizou o menu de sugestões depois de baixar as dependência do Spring Data JDBC. Tive que incluir o import na mão.
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

public record Transacao(
    @Id Long id, // Precisamos da anotação para usar com o Spring Data JDBC/JPA.
    Integer tipo,
    Date data,
    BigDecimal valor,
    Long cpf,
    String cartao,
    Time hora,

    // OBS importante: O H2 assume que os nomes são em letra maiúsculas, os nomes precisam estar assim também.
    @Column("DONO_LOJA") String donoDaLoja, // Especificando nome deferente no banco, o mesmo nome no schema.sql.
    @Column("NOME_LOJA") String nomeDaLoja) {

    // Wither Pattern
    // Esse método vai recriar a Transacao e mudar apenas a propriedade que eu preciso atualizar. Não é realmente uma atualização, já que record é imutável, é um objeto novo.
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