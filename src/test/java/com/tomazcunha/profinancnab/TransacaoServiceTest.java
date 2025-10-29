package com.tomazcunha.profinancnab;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tomazcunha.profinancnab.entity.Transacao;
import com.tomazcunha.profinancnab.repository.TransacaoRepository;
import com.tomazcunha.profinancnab.service.TransacaoService;

@ExtendWith(MockitoExtension.class) // Quando for usar o mockito, precisamos usar o runner do mockito, para que ele consiga injetar os mocks.
public class TransacaoServiceTest {
    // Classes com sufixo '...Test' representam classes com testes de unidade.
    // JUnity é o executor dos testes.

    @InjectMocks // Vai permitir injetar o service, e os mocks das suas dependências.
    private TransacaoService service; // Não podemos injetar de qualquer forma. Precisamos manter a frontera no teste de unidade, para isso precisamos usar o mock.
    // Vamos mockar as dependências para manter a unidade isolada nos testes.

    @Mock
    private TransacaoRepository repository;
    // Mock não é um objeto real, é um mais barato e fictício que representa o repository.
    // Se chamarmos qualquer método desse mock, não teremos resultados reais.
    // Precisamos pegar o mock repository e dizer o que ele precisa retornar quando chamarmos seus métodos.


    @Test // Os métodos dos testes sempre serão void.
    public void testlistTotaisTransacoesPorNomeDaLoja() {
        // assertTrue(true);
        // Rodando TransacaoServiceTest, vai aparecer um sinal check, se o teste passar. Estou confirmando que o teste passou(true).
        // assertTrue(false);
        // Caso não passe, aperece um X.

        // # ------------------------------------------------------------------------------
        // AAA - Arrange - O padrão AAA é usado para dividir nosso trabalho de testes.
        // 1 A (Arrange) = Precisamos montar os dados que serão usados para o teste executar. No caso, as transações.
        final String lojaA = "Loja A";
        final String lojaB = "Loja B";

        // Criando nossa primeira transação que será usada no teste.
        var transacao1 = new Transacao(
                1L,
                1,
                new Date(System.currentTimeMillis()),
                BigDecimal.valueOf(100),
                123456789L,
                "1234-5678-9012-3456",
                new Time(System.currentTimeMillis()),
                "Dono da loja A",
                lojaA);

        var transacao2 = new Transacao(
                2L,
                1,
                new Date(System.currentTimeMillis()),
                BigDecimal.valueOf(50.00),
                565432423L,
                "5678-3456-1234-9012",
                new Time(System.currentTimeMillis()),
                "Dono da loja B",
                lojaB);

        var transacao3 = new Transacao(
                3L,
                1,
                new Date(System.currentTimeMillis()),
                BigDecimal.valueOf(75.00),
                111444777L,
                "9012-1234-5678-3456",
                new Time(System.currentTimeMillis()),
                "Dono da loja A",
                lojaA);


        var mockTransacoes = List.of(transacao1, transacao2, transacao3);

        // Stub = Vamos simular o que é retornado quando o método do mock é invocado.
        // when (Quando esse método for chamado.)
        // thenReturn (Retorna esse objetos criados/mockados, em vez dos originais do banco)
        when(repository.findAllByOrderByNomeDaLojaAscIdDesc()).thenReturn(mockTransacoes);
        // A lógica do service ainda está sendo executada, mas quando chega nas dependências, a gente mocka o comportamento.



        // # ------------------------------------------------------------------------------
        // AAA - Act - Vamos para o segundo A que significa agir. Chamar a operação que está sendo testada.
        var reports = service.listTotaisTransacoesPorNomeDaLoja();
        // Vamos ter o resultado o teste do método que está sendo testado 'listTotaisTransacoesPorNomeDaLoja'.


        // # ------------------------------------------------------------------------------
        // AAA - Assert - Para afirmar/confirmar o resultado esperado.

        assertEquals(2, reports.size()); // Conformando se no meu report existe os dois agrupamentos de lojas.

        reports.forEach(report -> {
            if (report.nomeDaLoja().equals(lojaA)) {
                assertEquals(2, report.transacoes().size()); // Afirmando/Confirmando que existem 2 transações.
                assertEquals(BigDecimal.valueOf(175.0), report.total()); // Afirmando que o total é 175.

                // Afirmando que a atual loja está nas transações 1 e 3.
                assertTrue(report.transacoes().contains(transacao1));
                assertTrue(report.transacoes().contains(transacao3));


            } else if (report.nomeDaLoja().equals(lojaB)) {
                assertEquals(1, report.transacoes().size()); // Afirmando 1 transação.
                assertEquals(BigDecimal.valueOf(50.0), report.total()); // Afirmando que o total da Loja B é 50.

                assertTrue(report.transacoes().contains(transacao2)); // Afirmando o objeto da Loja B.
            }
        });
    }

}
