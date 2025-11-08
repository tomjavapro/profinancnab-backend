

# https://github.com/giuliana-bezerra/desafio-backend-pagnet/blob/main/backend/Dockerfile

    # FROM eclipse-temurin:17.0.8.1_1-jdk-jammy
    # VOLUME /tmp
    # COPY . .
    # RUN ./mvnw clean install -DskipTests
    # ENTRYPOINT ["java","-jar","target/desafio-backend-pagnet-0.0.1-SNAPSHOT.jar"]


    # ------------------------------------------------------------------------------
# # Imagem com os recursos para executar a aplicação.
# # jammy é compativel com arquitetura ARM e AMD.
# FROM eclipse-temurin:17.0.8.1_1-jdk-jammy

# # Copiando tudo que está na raiz do backend para a raiz do container.
# COPY . .

# # Build
# RUN ./gradlew clean bootJar

# # Comando para executar a aplicação.
# ENTRYPOINT [ "java", "-jar", "build/libs/profinancnab-0.0.1-SNAPSHOT.jar" ]


# ------------------------------------------------------------------------------
# # Copiando apenas o jar para o container. O jar precisa ser gerado no start.sh

# # Imagem base Java 17
# # FROM openjdk:17-jdk-slim      # openjdk:17-jdk-slim: failed to resolve source metadata for docker.io/library/openjdk:17-jdk-slim: docker.io/library/openjdk:17-jdk-slim: not found

# # Alterando para esse que está OK (docker pull eclipse-temurin:17-jdk)
# FROM eclipse-temurin:17-jdk

# # Define diretório de trabalho
# # WORKDIR /app

# # Correção de erro. Cria o diretório temp antes do build
# # RUN mkdir -p temp && chmod +x ./gradlew && ./gradlew clean bootJar
# # RUN mkdir -p temp
# VOLUME /temp # Está causando erro.

# COPY . .

# # Build
# RUN ./gradlew clean bootJar
# # RUN ./gradlew bootJar

# # Copia o JAR da aplicação
# # COPY target/*.jar app.jar
# # COPY build/libs/profinancnab-0.0.1-SNAPSHOT.jar app.jar

# # Expõe porta 8080
# EXPOSE 8080

# # Comando para executar a aplicação
# # ENTRYPOINT ["java", "-jar", "app.jar"]
# ENTRYPOINT ["java", "-jar", "build/libs/profinancnab-0.0.1-SNAPSHOT.jar"]


# ------------------------------------------------------------------------------
# Copiando todo o projeto para o container

# Imagem base Java 17 - usando eclipse-temurin
# FROM eclipse-temurin:17-jdk

# Define diretório de trabalho
# WORKDIR /app

# Primeiro copia os arquivos necessários para o build
# COPY . .

# Torna o gradlew executável e faz o build
# RUN chmod +x ./gradlew && ./gradlew clean bootJar

# Expõe porta 8080
# EXPOSE 8080

# Comando para executar a aplicação
# ENTRYPOINT ["java", "-jar", "build/libs/profinancnab-0.0.1-SNAPSHOT.jar"]


# # ------------------------------------------------------------------------------
# # 08/11/2025 15:16

# FROM eclipse-temurin:17-jdk

# # Define diretório de trabalho
# WORKDIR /app

# # Cria o diretório temp na raiz do container (fora do WORKDIR)
# RUN mkdir -p /temp

# # Copia os arquivos do projeto
# COPY . .

# # Build da aplicação
# RUN chmod +x ./gradlew && ./gradlew clean bootJar

# # Expõe porta 8080
# EXPOSE 8080

# # Comando para executar a aplicação
# ENTRYPOINT ["java", "-jar", "build/libs/profinancnab-0.0.1-SNAPSHOT.jar"]


# ------------------------------------------------------------------------------
# 08/11/2025 15:29

FROM eclipse-temurin:17-jdk

# Define diretório de trabalho
WORKDIR /app

# Cria o diretório temp na raiz do container
RUN mkdir -p /temp

# Copia apenas os arquivos necessários para o build
# COPY gradlew .
# COPY gradle/ gradle/
# COPY build.gradle .
# COPY settings.gradle .
# COPY src/ src/

# Copia os arquivos do projeto
COPY . .

# Remove ou substitui o gradle.properties problemático se existir
RUN if [ -f gradle.properties ]; then \
        sed -i '/org.gradle.java.home/d' gradle.properties || true; \
    fi

# Build da aplicação (usando o JDK do container)
RUN chmod +x ./gradlew && ./gradlew clean bootJar --no-daemon

# Expõe porta 8080
EXPOSE 8080

# Comando para executar a aplicação
ENTRYPOINT ["java", "-jar", "build/libs/profinancnab-0.0.1-SNAPSHOT.jar"]