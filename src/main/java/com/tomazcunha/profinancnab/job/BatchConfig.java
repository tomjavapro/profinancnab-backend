package com.tomazcunha.profinancnab.job;

import java.math.BigDecimal;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import com.tomazcunha.profinancnab.entity.Transacao;
import com.tomazcunha.profinancnab.entity.TransacaoCNAB;

@Configuration
public class BatchConfig {
    // precisamos configurar alguns componentes que farão esse processamento em lote.

    // Como Spring Batch trabalha com percistência em bando de dados, precisamo injetar esse componete:
    private PlatformTransactionManager transactionManager;
    private JobRepository jobRepository; // Passando a injetar no construtor, evitar ter que passar como parâmetro nas Beans.


    public BatchConfig(PlatformTransactionManager transactionManager, JobRepository jobRepository) {
        this.transactionManager = transactionManager;
        this.jobRepository = jobRepository;
    }

    // Criação dos principais componentes da aplicação.
    // Spring Batch trabalha com um conceito chamado Job, a tarefa que será processada.
    // O Spring executa o Bean Job por padrão.
    @Bean
    // Job job(Step step, JobRepository jobRepository) {
    Job job(Step step) { // Removendo jobRepository, agora está no construtor.
        // Primeira tarefa é importar o arquivo CNAB.
        // Job é dividido em steps, mas essa tarefa terá apenas uma etapa.
        // Precisamos também do JobRepository.
        // O funcionamento da aquitetura de batch é muito baseado em máquina de estados. Existem metadados que são usados para saber em que Step eu estou, o que já foi escrito, o que já foi lido no Job, e tudo isso é salvo no componente JobRepository. Por isso precisamos passa(injetando) o JobRepository ao criar o Job.

        // O JobBuilder é usado para configurar o nosso Job.
        return new JobBuilder("jobe", jobRepository)
            .start(step) // Iniciando o Step
            .incrementer(new RunIdIncrementer()) // Configurando para rodar mais de uma vez nessa etapa de teste, normalmente o Job só roda uma vez.
            .build(); // Componete Job criado e configurado.
    }

    // Criando nosso Step
    @Bean
    // Step step(JobRepository jobRepository) {
    // Step step(ItemReader reader, ItemProcessor processor, ItemWriter writer) { // Removendo jobRepository, agora está no construtor.
    Step step(
        ItemReader<TransacaoCNAB> reader,
        ItemProcessor<TransacaoCNAB, Transacao> processor, // Recebe TransacaoCNAB e transforma em uma Transacao.
        ItemWriter<Transacao> writer) { // Agora incluindo os tipos.
        // step precisa receber 3 conponentes especiais: tarefa de leitura, processamento, escrita.

        return new StepBuilder("step", jobRepository)
            // .chunk(1000, transactionManager) // chunk é o pedaço que será processado. O lote de dados é dividido em pedaços a serem processados. 1000 é o tamanho do pedaço. Para cada pedaço processado e commitado, eu preciso saber qual gerenciador de transição eu vou usar, transactionManager já foi injetado, o framework já configura com base no bando (h2 por enquanto).
            .<TransacaoCNAB, Transacao>chunk(1000, transactionManager) // Agora <Entrada, Saída>
            .reader(reader) // Informando quem é o reader, etc.
            .processor(processor)
            .writer(writer)
            .build(); // Precisamos definir os tipos de dados que serão processado, para evitar os warnings.
    }


    // Detalhando cada componente que faz parte do Step.
    // Criando a pasta 'files' e incluindo o arquivo 'files/CNAB.txt'

    // Criando um leitor
    @StepScope // Preciso estar em um contexto de StepScope para poder acessar 'jobParameters'. Essa é uma anotação do Spring Bacth para injetar resource apenas quando estiver disponível.
    @Bean
    // Para arquivo com formato indefinido, diferente do XML e JSON.
    // FlatFileItemReader<TransacaoCNAB> reader() {
    FlatFileItemReader<TransacaoCNAB> reader(
            @Value("#{jobParameters['cnabFile']}") Resource resource
            // Obtendo os parêmtros do Job com o nome do parâmetro 'cnabFile'.
            // Agora o resource vai ser injetado automaticamente.
        ) {

        // O return contem a lógica de leitura, implementado pelo 'FlatFileItemReaderBuilder' que vai devolver um objeto preenchido do tipo 'TransacaoCNAB'.
        return new FlatFileItemReaderBuilder<TransacaoCNAB>() // Padrão builder para ajudar na criação.
            .name("reader")
            // .resource(new FileSystemResource("/home/tomaz/1_Tomaz/Projetos4_Java/0_Desafios/Giuliana-Bezerra-Sistema-de-Pagamentos/profinancnab/files/CNAB.txt")) // O arquivo que vai ser lido. Caminho obsoluto.
            // .resource(new FileSystemResource("files/CNAB.txt")) // Usando caminho relativo.
            .resource(resource) //
            .fixedLength() // Que tipo de arquivo flat é esse? Tamanha fixo.
            .columns(
                new Range(1, 1),
                new Range(2, 9),
                new Range(10, 19),
                new Range(20, 30),
                new Range(31, 42),
                new Range(43, 48),
                new Range(49, 62),
                new Range(63, 80)
            ) // Permite informar as faixas de valores mostrada na documentação.
            .names(
                "tipo",
                "data",
                "valor",
                "cpf",
                "cartao",
                "hora",
                "donoDaLoja",
                "nomeDaLoja"
            ) // Declarando os nomes dos campos que correspondem as faixas de valores. Precisa ser os mesmos nomes que estão em TransacaoCNAB.
            .targetType(TransacaoCNAB.class)
            .build();
    }

    @Bean
    ItemProcessor<TransacaoCNAB, Transacao> processor() {
        // ItemProcessor é uma interface funcional, podemos usar a sintaxe de lambda functions para listar a cada item processado.
        return item -> {
            // item é do tipo TransacaoCNAB, então vamos criar uma Transacao a partir dos dados do item.

            // Não vamos apenas transformar um objeto em outro, precisamos formatar/normalizar os valores, data para o tipo data. Como estamos usando Record que é um objeto imutável, vamos usar um padrão para trabalhar com imutabilidade (Wither pattern).
            // Wither (murchar, secar, intimidar, mirrar, perder vigor)

            // Wither pattern
            var transacao = new Transacao(
                null,
                item.tipo(),
                null,
                // null, // valor recebia null
                item.valor().divide(BigDecimal.valueOf(100)),
                item.cpf(),
                item.cartao(),
                null,
                item.donoDaLoja().trim(),
                item.nomeDaLoja().trim()
            )
            // .withValor(item.valor().divide(BigDecimal.valueOf(100))) // Com o método criado no record, agora podemos passar o valor normalizado. Valor / 100. De acordo com a especificação, ele é um valor já multiplicado por 100, então precisamos dividir por 100 para normalizar em decimal.
            // Alterando a passagem do valor para em vez de chamar '.withValor', passar direto a divisão no construtor. Isso porque não vamos mudar o valor, não vamos precisar fazer parses como na data e hora que possuem uma lógica de negócio.
            // No '.withValor()', vai ser necessário mais afrente, que o valor seja puro, sem a divisão, por isso que essa divisão não está no '.withValor()'.
            .withData(item.data())
            .withHora(item.hora());

            return transacao;
        };

    }

    @Bean
    JdbcBatchItemWriter<Transacao> writer(DataSource dataSource) {
        // Vai escrever no banco de dados, injetando o DataSource.

        return new JdbcBatchItemWriterBuilder<Transacao>()
            .dataSource(dataSource)
            .sql("""
                insert into transacao (
                    tipo,
                    data,
                    valor,
                    cpf,
                    cartao,
                    hora,
                    dono_loja,
                    nome_loja
                ) values (
                    :tipo,
                    :data,
                    :valor,
                    :cpf,
                    :cartao,
                    :hora,
                    :donoDaLoja,
                    :nomeDaLoja
                )
            """) // Os placeholders(:tipo) estão com os nomes iguais aos ??Beans??(ou campos) de Transacao. Por isso podemos usar 'beanMapped()'.
            .beanMapped() // Vai mapear esse placeholders preenchendo com os valores do objeto Transacao recebido
            .build();
    }


    // Configurando o JobLauncher para ser assíncrono.
    @Bean // Agora temo jobLauncherAsync no contexto de injeção do Spring. JobLauncher Gera um Bean dentro do container.
    JobLauncher jobLauncherAsync(JobRepository jobRepository) throws Exception {
        var jobLauncher = new TaskExecutorJobLauncher();
        jobLauncher.setJobRepository(jobRepository);
        jobLauncher.setTaskExecutor(new SimpleAsyncTaskExecutor()); // É o taskExecutor que diz se a tarefa é síncrono ou assíncrono.
        jobLauncher.afterPropertiesSet(); // throws Exception
        return jobLauncher;
    }

}
