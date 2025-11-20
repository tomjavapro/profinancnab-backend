# ProFin CNAB - Sistema de Processamento de Arquivos de Transações Financeiras CNAB (Backend)

![Badge de Licença](https://img.shields.io/badge/license-MIT-blue.svg)

Backend desenvolvido em Java e Spring Boot para processar arquivos de transações financeiras no formato CNAB (Centro Nacional de Automação Bancária).

## 📖 Visão Geral

A aplicação utiliza o Spring Batch para implementar um fluxo de trabalho robusto de leitura, processamento e gravação de dados. O sistema é projetado para receber um arquivo `.txt` contendo transações, normalizar essas informações e persisti-las em um banco de dados relacional.

O processamento do job é iniciado através de um endpoint REST, permitindo a execução assíncrona da tarefa.

## ✨ Funcionalidades

- **Leitura de Arquivo CNAB:** Utiliza `FlatFileItemReader` para ler arquivos de texto com layout de colunas de tamanho fixo.
- **Processamento de Transações:** Normaliza os dados de cada transação, ajustando valores monetários, convertendo formatos de data/hora e tratando os tipos de transação (débito, crédito, etc.).
- **Persistência em Banco de Dados:** Grava as transações processadas em um banco de dados usando `JdbcBatchItemWriter`.
- **Execução Assíncrona:** O job do Spring Batch é executado em uma thread separada para não bloquear a API.

## 🏗️ Decisões de Arquitetura

- O controle de unicidade das transações é feito por arquivo CNAB, o que significa que o processamento das transações é feito apenas uma vez por arquivo.
- O arquivo CNAB deve ser nomeado com um id ou timestamp, pois ele será passado como
parâmetro do job e só pode ser importado uma única vez.
- Caso seja informado um arquivo já importado, deve ser informada uma mensagem de
erro ao usuário.
- Caso haja erro no processamento é possível submeter o mesmo arquivo várias vezes para habilitar o restart de onde o processamento parou.
- Se o arquivo for muito grande, é possível utilizar uma estratégia de particionamento
no job, melhorando assim a performance.

## 🛠️ Tecnologias Utilizadas

#### **Backend** ([profinancnab-backend/README.md](https://github.com/tomjavapro/profinancnab-backend/blob/main/README.md))
- **Java 17+**
- **Spring Boot**
- **Spring Batch** (para processamento em lote)
- **Spring Web** (para a exposição de endpoints REST)
- **Spring Data JDBC**
- **PostgreSQL** (banco de dados principal)
- **H2 Database** (para testes e desenvolvimento local)
- **Gradle** (gerenciador de dependências e build)
- **Docker** e **Docker Compose** (para orquestração de ambientes)

#### **Frontend** ([profinancnab-frontend/README.md](https://github.com/tomjavapro/profinancnab-frontend/blob/main/README.md))
- **React** (19.1.1)
- **Vite** (7.1.7)
- **Tailwindcss** (4.1.16)
- **JavaScript**
- **HTML5**
- **CSS3**
- **Font Awesome** (para ícones)
- **Docker**

## Estrutura do Projeto

```
profinancnab/
├── src/
│   ├── main/
│   │   ├── java/com/tomazcunha/profinancnab/
│   │   │   ├── entity/      # Entidades (Transacao, TransacaoCNAB)
│   │   │   ├── job/         # Configuração do Spring Batch (BatchConfig)
│   │   │   ├── controller/  # Endpoints REST para iniciar o job
│   │   │   └── ...
│   │   └── resources/
│   │       ├── application.properties  # Configurações da aplicação
│   │       └── schema.sql              # Schema inicial do banco
│   └── test/
├── build.gradle            # Arquivo de build do Gradle
└── gradlew                   # Gradle Wrapper
```

## 🚀 Como Executar

### Pré-requisitos

- JDK 17 ou superior
- Docker e Docker Compose (para executar com PostgreSQL)

### 1. Clonar o Repositório

```bash
git clone https://github.com/tomjavapro/profinancnab-backend
cd profinancnab-backend
```

### 2. Executar a Aplicação

O projeto pode ser executado localmente usando o Gradle Wrapper.

```bash
# No Linux ou macOS
./gradlew bootRun

# No Windows
./gradlew.bat bootRun
```

### 2.1. Executar com Docker

Para executar a aplicação em um contêiner Docker, você pode construir a imagem a partir do Dockerfile e depois iniciar um contêiner.

1. Construa a imagem Docker:
    ```
    docker build -t profinancnab-backend .
    ```

2. Execute o contêiner:
    ```
    docker run -p 8080:8080 profinancnab-backend
    ```



### 3. Iniciar o Processamento

Para iniciar o processamento de um arquivo CNAB, envie uma requisição `POST` para o endpoint `/cnab/upload` com o arquivo anexado como `multipart/form-data`.

**Exemplo com cURL:**

```bash
curl -X POST -F "file=@/caminho/para/seu/CNAB.txt" http://localhost:8080/cnab/upload
```

### 4. Executar o Frontend

- [profinancnab-frontend/README.md](https://github.com/tomjavapro/profinancnab-frontend/blob/main/README.md)


## 🧪 Testes

Os arquivos de teste para este projeto estão localizados em:
```
profinancnab/
├── src/
│   └── test/
```

A estratégia de testes foi dividida em duas categorias principais:

1.  **Testes de Unidade:** Focados em validar os componentes individuais do job do Spring Batch. Por exemplo, o `ItemProcessor` (`TransacaoProcessor`) é testado isoladamente para garantir que a lógica de normalização e transformação dos dados (como ajustes de valores, datas e remoção de espaços) está funcionando corretamente.

2.  **Testes de Integração:** Validam o fluxo completo do job (`end-to-end`). Utilizando o `JobLauncherTestUtils` do Spring Batch e um banco de dados em memória (H2), esses testes simulam a execução real do job. Eles verificam se o sistema consegue:
    - Ler um arquivo CNAB de exemplo.
    - Processar as transações sem erros.
    - Gravar os dados corretamente no banco de dados H2.

Essa abordagem garante tanto a correção da lógica de negócio em pequena escala quanto a integração robusta de todos os componentes do sistema.


## ⚙️ Deploy

Este projeto foi implantado utilizando os seguintes serviços:

- Backend (Spring Boot): Render.com
    - URL do Serviço: https://profinancnab-backend-webservice.onrender.com
    - Repositório GitHub: https://github.com/tomjavapro/profinancnab-backend

- Frontend: Render.com
    - URL do Serviço: https://profinancnab-frontend.onrender.com
    - Repositório GitHub: https://github.com/tomjavapro/profinancnab-frontend

- Banco de Dados (PostgreSQL): Neon.com
    - Um serviço de banco de dados PostgreSQL gerenciado foi utilizado para persistência dos dados.

A configuração de deploy no Render.com foi realizada para acessar diretamente os repositórios GitHub mencionados, facilitando a integração contínua e o deploy automático.


## 📷 Screenshots

![Print 01](screenshot/profinancnab-frontend.png "profinancnab-frontend")


## 📝 Licença

Este projeto está sob a licença MIT.