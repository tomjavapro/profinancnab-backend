package com.tomazcunha.profinancnab.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.tomazcunha.profinancnab.service.CNABService;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;


// Usando Spring Web
@RestController
@RequestMapping("cnab") // Não precisa mais incluir '/' no endpoint. Ex.: /cnab.
public class CNABController {

    private final CNABService service;

    // Injetando service
    public CNABController(CNABService service) {
        this.service = service;
    }

    @PostMapping("upload")
    // @CrossOrigin(origins = {"http://localhost:9090"}) // Permitindo essa origem chamar o backend. 9090 é a porta atual do frontend.
    // @CrossOrigin(origins = { "http://localhost:9096" })
    @CrossOrigin(origins = { "https://profinancnab-frontend.onrender.com" }) // Permissão para o deploy.
    public String upload(@RequestParam("file") MultipartFile file) throws Exception {
        service.uploadCnabFile(file); // Chamando service que fará o upload do arquivo.
        return "Processamento iniciado!";
    }
    // OBS: Dependendo de onde estou chamando curl, o caminho do 'fiel=@' vai mudar.
    // Terminal atualmente na raiz do projeto '/profinancnab'
    // <> curl -X POST -F "file=@files/CNAB.txt" http://localhost:8080/cnab/upload
    // Processamento iniciado!

    // Usando try simples.
    // @PostMapping("upload")
    // public String upload(@RequestParam("file") MultipartFile file) {
    //     try {
    //         service.uploadCnabFile(file); // Chamando service que fará o upload do arquivo.
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    //     return "Processamento iniciado!";
    // }

    // Recomendado
    // Assim eu tenho controle total sobre a resposta HTTP retornada ao cliente.
    // @PostMapping("upload")
    // public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) {
    //     try {
    //         service.uploadCnabFile(file);
    //         return ResponseEntity.ok("Processamento iniciado!");
    //     } catch (Exception e) {
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
    //                 .body("Erro no processamento: " + e.getMessage());
    //     }
    // }




}
