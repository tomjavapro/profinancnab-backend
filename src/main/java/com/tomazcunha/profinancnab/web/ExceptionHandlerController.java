package com.tomazcunha.profinancnab.web;

import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice // Usado para fazer tratamento de exceção genérico para todos os controladores.
public class ExceptionHandlerController {

    // Queremos lidar com uma exceção específica.
    // A exceção 'JobInstanceAlreadyCompleteException' é lançada quando executo um job mais de uma vez com os mesmos conjustos de parâmetros.
    // Capturar qualquer exceção do tipo 'JobInstanceAlreadyCompleteException' que for mostrada por um dos meus controladores.
    @ExceptionHandler(JobInstanceAlreadyCompleteException.class)
    private ResponseEntity<Object> handleFileAlreadyImported(JobInstanceAlreadyCompleteException exception) {
        // Retornando uma entidade de resposta.
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body("O arquivo informado já foi importado no sistema!");
    }
}
