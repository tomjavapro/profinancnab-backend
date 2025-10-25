package com.tomazcunha.profinancnab.domain;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

// Vamos criar a lógica de upload de arquivos.
@Service
public class CNABService {
    private final Path fileStoregeLocation; // O caminho onde o arquivo temporário ficará.

    // Depois do Bean Job desabilitado, precisamos dessa forma para executar o Job.
    private final JobLauncher jobLauncher;
    // Por padrão o executor de tarefas padrão é o default, então vamos configurar 'jobLauncher' para execurar de forma assíncrona.

    // Precisamos agora executar o Job para processa-lo.
    private final Job job; // Injetando
    // Por padrão o Spring executa o Job quando econtra '@Bean Job job(Step step)'.
    // Precisamos desabilitar a execução padrão do Bean Job, no arquivo properties.

    // file.upload-dir será a propriedade no 'aplication.properties' que conterá o caminho do diretório temporário.
    // Incluindo agora os novos atributos.
    public CNABService(
        @Value("${file.upload-dir}") String fileUploadDir,
        @Qualifier("jobLauncherAsync") JobLauncher jobLauncher, // Qualificando com o nome do Bean jobLauncherAsync, para diferencia o JobLauncher padrão do Spring.
        Job job) {

        this.fileStoregeLocation = Paths.get(fileUploadDir); // Construtor instânciando com o diretório do arquivo.
        this.jobLauncher = jobLauncher;
        this.job = job;
    }

    // Vamor criar um diretório 'temp' para apenas processar o arquivo.
    public void uploadCnabFile(MultipartFile file) throws Exception {
        var fileName = StringUtils.cleanPath(file.getOriginalFilename()); // Pegando apenas o nome do arquivo.
        var targetLocation = fileStoregeLocation.resolve(fileName); // Resolvendo o diretório mais o nome do arquivo.
        // try {
        //     // Tranferindo o arquivo para o diretório alvo.
        //     file.transferTo(targetLocation); // Pode lançar uma exceção
        // } catch (IllegalStateException e) {
        //     e.printStackTrace();
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }
        file.transferTo(targetLocation); // Mudei para throws Exception en vez do try-catch.

        var jobParameters = new JobParametersBuilder()
            .addJobParameter(
                "cnab", // Controle de unicidade de execução no spring batch, se passar o cnab com o valor 1, e roda com sucesso, se tentar passar o 1 novamente, como o "cnab" é o identificador, não deixa rodar novamente.
                file.getOriginalFilename(),
                String.class,
                true    // O parâmetro identificador no Spring Batch é usado como chave para não permitir execuções repetidas daquele job.
            )
            .addJobParameter(
                "cnabFile",
                "file:" + targetLocation.toString(),
                String.class
            )
            .toJobParameters(); // Tranformando em parâmetros do job.

        // Agora com o jobLauncher no contrutor, podemos executar o job.
        jobLauncher.run(job, jobParameters);
        // Recebera com parâmetro o nome de um arquivo, porque um arquivo CNAB só vai ser processado uma única vez, a transação não pode ser duplicada de um mesmo arquivo.
        // jobParameters receberá o nome do arquivo que será processado.

    }

}
