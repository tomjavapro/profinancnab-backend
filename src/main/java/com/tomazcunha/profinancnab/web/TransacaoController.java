package com.tomazcunha.profinancnab.web;

import java.util.List;

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
    //     return service.IterableTotaisTransacoesPorNomeDaLoja();
    // }
    // ./gradlew bootRun

    // Incluindo primeiro os registros
    // curl -X POST -F "file=@files/CNAB.txt" http://localhost:8080/cnab/upload
    // curl -X POST -F "file=@files/CNAB-2.txt" http://localhost:8080/cnab/upload

    // Testando no terminal e firefox
    // curl http://localhost:8080/transacoes


    // Voltando a receber Lista e não mais Iterable
    @GetMapping
    List<Transacao> listAll() {
        // Não estou usando o 'listbleTotaisTransacoesPorNomeDaLoja' por ter criado novo método.
        return service.listTotaisTransacoesPorNomeDaLoja();
    }
    // Rodando para testar
    // ./gradlew bootRun
    // curl -X POST -F "file=@files/CNAB.txt" http://localhost:8080/cnab/upload
    // curl http://localhost:8080/transacoes
    // Ok, Agora todas 'nomeDaLoja "BAR DO JOÃO"' estão vindo primeiro,
    // depois passa a vir 'nomeDaLoja "LOJA DO Ó - FILIAL"', e assim vai.

}
