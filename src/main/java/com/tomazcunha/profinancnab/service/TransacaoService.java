package com.tomazcunha.profinancnab.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.stereotype.Service;

import com.tomazcunha.profinancnab.entity.TipoTransacao;
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
    // Precisamos agora gerar o relatório, fazer o agrupamento
    // public List<Transacao> listTotaisTransacoesPorNomeDaLoja() {
    public List<TransacaoReport> listTotaisTransacoesPorNomeDaLoja() { // Novo retorno é uma lista de TransacaoReport.
        // Para trazer os valores, precisamos dos dados do banco. Precisamos criar os repositórios.
        // var transacoes = repository.findAll();
        var transacoes = repository.findAllByOrderByNomeDaLojaAscIdDesc();

        // Como estão retornando ordenado pelo nome da loja, podemos criar a lógica para retornar de forma agrupada.
        var reportMap = new LinkedHashMap<String, TransacaoReport>();
        // reportMap será o relatório.
        // O String vai ter o nome da loja, vai ser o fator de agrupamento.
        // TransacaoReport é a transação para aquela loja.
        // LinkedHashMap preserva a ordem para o relatório, HashMap não preserva a ordem.

        transacoes.forEach(transacao -> { // Percorrer cada transacao em transacoes.
            String nomeDaLoja = transacao.nomeDaLoja(); // Captura o nome da loja

            // Agora vamos multiplicar pelo sinal. Com isso não precisamos incluir um "if do tipo" em cada sérvice que manipular transação, agora está centralizado no enum (com switch). O teste só vai ser feito uma unica vez.
            // var tipoTransacao = TipoTransacao.findByTipo(transacao.tipo());
            // BigDecimal valor = transacao.valor().multiply(tipoTransacao.getSinal()); // multiply só pode ser feita com BigDecimal
                // Agora a lógica de normalização "var tipoTransacao" e "BigDecimal valor" estão em "BatchConfig.processor".
                // Com isso, poderá a normalização também ser gravada no banco.

            var valor = transacao.valor(); // Agora apenas pegando o valor, sem modificação, já que já vem normalizado do banco.

            // Estamos criando o novo Hash de TransacaoRepost(reportMap) e para cada trasação(de uma loja espeífica) buscada do banco, vamos adicionar nesse novo report, fazendo a inclusão se essa transação dessa loja ainda não existir, ou se já existir, somar o valor e adicionar a lista de transação dessa loja.
            // key vai ser o nome da loja
            // Vamos ter na função lamda, as devidas verificações para fazer o mapeamento
            // key = chave do mapa
            // existingReport = relatório existente, caso ele exista
            // Se na chave do mapa "nomeDaLoja" existe algo, vamos manter "existingReport" e depois adicionar o saldo
            // Sa a chave key não existe, vamos inicializar com o saldo Zero, para fazer depois as alteraçoes de soma
            reportMap.compute(nomeDaLoja, (key, existingReport) -> {
                // nomeDaLoja: String ("Loja A")
                // key: String (É UMA CÓPIA do nomeDaLoja)
                // existingReport: TransacaoReport (valor associado a "Loja A" no mapa)

                // Primeira transação para "Loja A"
                    // key = "Loja A" (String)
                    // existingReport = null (não existe ainda)
                    // Cria: TransacaoReport{nomeDaLoja: "Loja A", total: 0, transacoes: []}
                    // Retorna: TransacaoReport{nomeDaLoja: "Loja A", total: 100, transacoes: [transacao1]}
                // Segunda transação para "Loja A"
                    // key = "Loja A"
                    // existingReport = TransacaoReport{nomeDaLoja: "Loja A", total: 100, transacoes: [transacao1]}
                    // Reutiliza o objeto existente!
                    // Retorna: TransacaoReport{nomeDaLoja: "Loja A", total: 150, transacoes: [transacao1, transacao3]}

                // O Map fica assim:
                // {
                //     "Loja A" → TransacaoReport{nomeDaLoja: "Loja A", total: 150, transacoes: [transacao1, transacao3]},
                //     "Loja B" → TransacaoReport{nomeDaLoja: "Loja B", total: 200, transacoes: [transacao2]}
                // }


                // O método compute() é uma operação do Map que permite:
                //     Buscar um valor pela chave
                //     Modificar ou criar esse valor de forma atômica
                //     Retornar o novo valor
                // Vantagens do compute():
                //     Thread-safe: Operação atômica (evita condições de corrida)
                //     Código conciso: Substitui várias operações (get, check, put)
                //     Eficiente: Uma única operação no mapa
                // compute() É uma forma elegante de fazer "upsert" (update ou insert) em maps!
                // compute(K key, BiFunction function)
                //     key (chave)         Necessária. Especifica a chave da entrada.
                //     function            Necessária. Um objeto BiFunction ou uma expressão lambda que calcula o valor da entrada. O primeiro parâmetro da função contém a chave de uma entrada e o segundo parâmetro contém seu valor.
                // 1 Para cada transação, pega o nomeDaLoja como chave
                // 2 Se a loja já existe no mapa:
                //     existingReport contém o relatório existente
                //     Reutiliza o relatório: report = existingReport
                // 3 Se a loja NÃO existe no mapa:
                //     existingReport é null
                //     Cria um novo relatório: new TransacaoReport(key, BigDecimal.ZERO, new ArrayList<>())
                // 4 Atualiza o relatório:
                //     report.addTotal(valor) - soma o valor da transação ao total
                //     report.addTransacao(transacao) - adiciona a transação à lista
                // 5 Retorna o relatório atualizado para ser armazenado no mapa

                // Se existir Report, mantem o report
                // Se não existir, cria um novo record TransacaoReport, passando os valores dos campos
                var report = (existingReport != null) ? existingReport : new TransacaoReport(key, BigDecimal.ZERO, new ArrayList<>());
                // Key = String nomeDaLoja.

                // Se existe algum lançamento nessa chave(nome da loja)
                // Se for a primeira vez, cria-se um report vazio
                // Nas próximas, vamos pegar o relatório existente e incrementar o saldo dele para totalizar o valor de transações daquela loja

                // "var report" é um objeto imutário, mas precismos adicionar o saldo nele, por isso vamos criar novas funções em TransacaoReport.

                // return report.addTotal(valor);
                // return report.addTotal(valor).addTransacao(transacao);
                // Valor adicionado. O relatório será montado com esses totais e o detalhe de cada transação que levou a obter esse total, por isso, também precisamos adicional a tranação. Vamos criar addTransacao em TransacaoReport.
                // Agora temos um relatória preenchido agrupando totais e transações respectivas por loja.

                // Atualizando o record.
                // Modificando para os valores já serem mostradados da forma negativa ou positiva.
                // Agora com os números negativos no valor, não só no total.
                // return report.addTotal(valor).addTransacao(transacao.withValor(valor));
                return report.addTotal(valor).addTransacao(transacao); // Não estou mais gravando no record 'Transacao' o valor normalizado, ele agora já foi normalizado antes de ser gravado no banco.

            });
        });

        // return transacoes; // List<Transacao>
        return new ArrayList<>(reportMap.values()); // List<TransacaoReport>
            // Transformando os valores do LinkedHashMap em um ArrayList<TransacaoReport>
            // Retorna uma Collection<TransacaoReport> com todos os valores do Map
            // NÃO inclui as chaves (nomeDaLoja)
            // Exemplo dos valores da lista:
                // [0] = TransacaoReport{"Loja A", total: 150, transacoes: [t1, t3]}
                // [1] = TransacaoReport{"Loja B", total: 200, transacoes: [t2]}
                // [2] = TransacaoReport{"Loja C", total: 75, transacoes: [t4]}
    }

    // Testando a consulta no banco
    public Iterable<Transacao> IterableTotaisTransacoesPorNomeDaLoja() {
        var transacoes = repository.findAll();
        return transacoes;
    }






}
