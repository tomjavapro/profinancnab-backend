package com.tomazcunha.profinancnab.domain;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

// Vamos criar a lógica de upload de arquivos.
@Service
public class CNABService {
    private final Path fileStoregeLocation; // O caminho onde o arquivo temporário ficará.

    // file.upload-dir será a propriedade no 'aplication.properties' que conterá o caminho do diretório temporário.
    public CNABService(@Value("${file.upload-dir}") String fileUploadDir) {
        this.fileStoregeLocation = Paths.get(fileUploadDir); // Construtor instânciando com o diretório do arquivo.
    }

    // Vamor criar um diretório 'temp' para apenas processar o arquivo.
    public void uploadCnabFile(MultipartFile file) {
        var fileName = StringUtils.cleanPath(file.getOriginalFilename()); // Pegando apenas o nome do arquivo.
        var targetLocation = fileStoregeLocation.resolve(fileName); // Resolvendo o diretório mais o nome do arquivo.
        try {
            // Tranferindo o arquivo para o diretório alvo.
            file.transferTo(targetLocation); // Pode lançar uma exceção
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
