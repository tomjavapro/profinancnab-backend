package com.tomazcunha.profinancnab.web;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tomazcunha.profinancnab.entity.Transacao;
import com.tomazcunha.profinancnab.entity.TransacaoReport;
import com.tomazcunha.profinancnab.service.TransacaoService;

@RestController
@RequestMapping("transacoes")
public class TransacaoController {

    // Método de consulta
    // @GetMapping
    // List<TransacaoReport> listAll() {

    // }


    private final TransacaoService service;

    public TransacaoController(TransacaoService service) {
        this.service = service;
    }

    // Testando acesso ao banco
    // @GetMapping
    // Iterable<Transacao> listAll() {
    //     // Não estou usando o 'listbleTotaisTransacoesPorNomeDaLoja' por ter criado novo método.
    //     return service.iterableTotaisTransacoesPorNomeDaLoja();
    // }
    // ./gradlew bootRun

    // Incluindo primeiro os registros
    // curl -X POST -F "file=@files/CNAB.txt" http://localhost:8080/cnab/upload
    // curl -X POST -F "file=@files/CNAB-2.txt" http://localhost:8080/cnab/upload

    // Testando no terminal e firefox
    // curl http://localhost:8080/transacoes


    // Voltando a receber Lista e não mais Iterable
    @GetMapping
    // @CrossOrigin(origins = {"http://localhost:9090"})
    // @CrossOrigin(origins = {"http://localhost:9096"})  // Alterando para rodar no container.
    @CrossOrigin(origins = {"https://profinancnab-frontend.onrender.com"})  // Permissão para o deploy.
        // Permitindo essa origem chamar o backend. 9090 é a porta atual do frontend.
        // # Esse erro de CORS vai acontecer quando temos duas aplicações (Frontend e Backend) executando no mesmo domínio (no caso localhost, mudando apenas a porta), isso é uma proteção incluída nos navegadores para evitar que pessoas mal intencionadas executem rotinas que não deveriam rodar no enderelo original.
    // List<Transacao> listAll() {
    List<TransacaoReport> listAll() { // Novo retorno é uma lista de TransacaoReport.
        // Não estou usando o 'listbleTotaisTransacoesPorNomeDaLoja' por ter criado novo método.
        return service.listTotaisTransacoesPorNomeDaLoja();
    }
    // Rodando para testar
    // ./gradlew bootRun
    // curl -X POST -F "file=@files/CNAB.txt" http://localhost:8080/cnab/upload
    // curl http://localhost:8080/transacoes
    // Ok, Agora todas 'nomeDaLoja "BAR DO JOÃO"' estão vindo primeiro,
    // depois passa a vir 'nomeDaLoja "LOJA DO Ó - FILIAL"', e assim vai.

    // Novo teste com TransacaoReport
    // Consultar por Nome de Loja
    // Agrupar por Nome de Loja
    // ./gradlew bootRun
    // curl -X POST -F "file=@files/CNAB.txt" http://localhost:8080/cnab/upload
    // curl http://localhost:8080/transacoes | jason_pp

}
