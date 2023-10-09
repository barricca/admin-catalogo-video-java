<center>
  <p align="center">
    <img src="https://user-images.githubusercontent.com/20674439/158480514-a529b310-bc19-46a5-ac95-fddcfa4776ee.png" width="150"/>&nbsp;
    <img src="https://icon-library.com/images/java-icon-png/java-icon-png-15.jpg"  width="150" />
  </p>  
  <h1 align="center">? Microservi?o: Admin do Cat?logo de V?deos com Java</h1>
  <p align="center">
    Microservi?o referente ao backend da Administra??o do Cat?logo de V?deos<br />
    Utilizando Clean Architecture, DDD, TDD e as boas pr?ticas atuais de mercado
  </p>
</center>
<br />

## Ferramentas necess?rias

- JDK 17
- IDE de sua prefer?ncia
- Docker

## Como executar?

1. Clonar o reposit?rio:
```sh
git clone https://github.com/devfullcycle/FC3-admin-catalogo-de-videos-java.git
```

2. Subir o banco de dados MySQL com Docker:
```shell
docker-compose up -d
```

3. Executar as migra??es do MySQL com o Flyway:
```shell
./gradlew flywayMigrate
```

> Tamb?m ? poss?vel executar como uma aplica??o Java atrav?s do
> m?todo main() na classe Main.java

## Banco de dados

O banco de dados principal ? um MySQL e para subir localmente vamos utilizar o
Docker. Execute o comando a seguir para subir o MySQL:

```shell
docker-compose up -d
```

Pronto! Aguarde que em instantes o MySQL ir? estar pronto para ser consumido
na porta 3306.

### Migra??es do banco de dados com Flyway

#### Executar as migra??es

Caso seja a primeira vez que esteja subindo o banco de dados, ? necess?rio
executar as migra??es SQL com a ferramenta `flyway`.
Execute o comando a seguir para executar as migra??es:

```shell
./gradlew flywayMigrate
```

Pronto! Agora sim o banco de dados MySQL est? pronto para ser utilizado.

<br/>

#### Limpar as migra??es do banco

? poss?vel limpar (deletar todas as tabelas) seu banco de dados, basta
executar o seguinte comando:

```shell
./gradlew flywayClean
```

MAS lembre-se: "Grandes poderes, vem grandes responsabilidades".

<br/>

#### Reparando as migra??es do banco

Existe duas maneiras de gerar uma inconsist?ncia no Flyway deixando ele no estado de repara??o:

1. Algum arquivo SQL de migra??o com erro;
2. Algum arquivo de migra??o j? aplicado foi alterado (modificando o `checksum`).

Quando isso acontecer o flyway ficar? em um estado de repara??o
com um registro na tabela `flyway_schema_history` com erro (`sucesso = 0`).

Para executar a repara??o, corrija os arquivos e execute:
```shell
./gradlew flywayRepair
```

Com o comando acima o Flyway limpar? os registros com erro da tabela `flyway_schema_history`,
na sequ?ncia execute o comando FlywayMigrate para tentar migrar-los novamente.

<br/>

#### Outros comandos ?teis do Flyway

Al?m dos comandos j? exibidos, temos alguns outros muito ?teis como o info e o validate:

```shell
./gradlew flywayInfo
./gradlew flywayValidate
```

Para saber todos os comandos dispon?veis: [Flyway Gradle Plugin](https://flywaydb.org/documentation/usage/gradle/info)

<br/>

#### Para executar os comandos em outro ambiente

L? no `build.gradle` configuramos o Flyway para l?r primeiro as vari?veis de
ambiente `FLYWAY_DB`, `FLYWAY_USER` e `FLYWAY_PASS` e depois usar um valor padr?o
caso n?o as encontre. Com isso, para apontar para outro ambiente basta sobrescrever
essas vari?veis na hora de executar os comandos, exemplo:

```shell
FLYWAY_DB=jdbc:mysql://prod:3306/adm_videos FLYWAY_USER=root FLYWAY_PASS=123h1hu ./gradlew flywayValidate
```

### Executando com Docker
Para rodar a aplica??o localmente com Docker, iremos utilizar o `docker compose` e necessita de apenas tr?s passos:
<br/>

#### 1. Gerando o artefato produtivo (jar)

Para gerar o artefato produtivo, basta executar o comando:
```
./gradlew bootJar
```
<br/>

#### 2. Executando os containers independentes

Para executar o MySQL e o Rabbit, basta executar o comando abaixo.
```
docker-compose up -d
```
<br/>

#### 3. Executando a aplica??o junto dos outros containers

Depois de visualizar que os demais containers est?o de p?, para rodar sua aplica??o junto basta executar o comando:
```
docker-compose --profile app up -d
```

> **Obs.:** Caso necessite rebuildar a imagem de sua aplica??o ? necess?rio um comando adicional:
>```
>docker compose build --no-cache app
>```

#### Parando os containers

Para encerrar os containers, basta executar o comando:
```
docker compose --profile app stop
```

### Keycloak

#### Setup

1. Adicionar no docker-compose o container do Keycloak
    ```
      keycloak:
        container_name: adm_videos_keycloak
        image: quay.io/keycloak/keycloak:20.0.3
        environment:
          - KEYCLOAK_ADMIN=admin
          - KEYCLOAK_ADMIN_PASSWORD=admin
        ports:
          - 8443:8080
        command:
          - start-dev
    ```
2. Subir o container e navegar ate `http://localhost:8443/`
3. Criar um realm novo para o projeto: `fc3-codeflix`
4. Navegar ate Realm settings > General > Endpoints
    - Esses endpoints s?o importantes para fazer-mos a integra??o
5. Navegar ate Realm settings > Keys
    - Iremos utilizar a chave publica do algoritmo RS256 para verificar o token
6. Criar o client:
    - Client Id: fc3-admin-catalogo-de-videos
    - Client authentication: ON -- isso faz acesso confidential
    - Redirect URL: confidential
    - Comentar das credentials `client and secret` que usaremos para login manual
7. Criar a role:
    - Role: catalogo-admin
    - Description: Role que d? permiss?o de admin para os usu?rios
8. Criar um group:
    - Name: catalogo-admin
    - Role mapping: assign `catalogo-admin`
9. Criar um usuario:
    - Nome: myuser
    - Groups: adicionar ao `catalogo-admin`
    - Criar um credentials: `123456`


#### Integration

1. Adicionar o starter do spring boot:
   ```
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    
    testImplementation('org.springframework.security:spring-security-test')
   ```
2. Configura??o das properties:
   ```properties
       keycloak:
           realm: fc3-codeflix
           host: http://localhost:8443
      
       spring:
           security:
               oauth2:
                   resourceserver:
                       jwt:
                           jwk-set-uri: ${keycloak.host}/realms/${keycloak.realm}/protocol/openid-connect/certs
                           issuer-uri: ${keycloak.host}/realms/${keycloak.realm}
   ```