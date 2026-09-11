# API Rest de Votação
API Rest desenvolvida em Java e Spring Boot para gerenciamento de pautas e associados, permitindo a abertura de votações e o registro e processamento de votos em assembleias.


## Funcionalidades
- Cadastro de pautas
- Cadastro de associados
- Abertura de votação de uma pauta
- Votação **SIM / NAO** pelo associado
- Consulta do resultado da votação

## Tecnologias
- Java 21
- Spring Boot 4.1.1
- PostgreSQL 16
- H2 (testes)
- Apache Kafka
- Spring Security + JWT
- Redis
- Flyway
- Docker
- JUnit + Mockito

**Interfaces**
- Kafka UI: http://localhost:8085
- RedisInsight: http://localhost:5540


## Arquitetura da Votação
A votação utiliza processamento assíncrono dos votos com Kafka e cache em Redis, a fim de garantir maior disponibilidade e eficiência no processamento das votações.


```text
                    ┌─────────────────────┐
                    │      API REST       │
                    └──────────┬──────────┘
                               │
                ┌──────────────┴──────────────┐
                │                             │
         Abrir votação                      Votar
                │                             │
                ▼                             ▼
           ┌────────┐                 ┌───────────────┐
           │ Redis  │                 │     Kafka     │
           └────┬───┘                 └───────┬───────┘
    Expira no final da votação                │
                                     ┌────────▼────────┐
                                     │     Consumer    │
                                     └────────┬────────┘
                                              │
                                     ┌────────▼────────┐
                                     │      Redis      │
                                     └────────┬────────┘
                                              │
                                     ┌────────▼────────┐
                                     │  Voto válido?   │
                                     └───────┬─────────┘
                                         Sim │ Não
                                             │
                               ┌─────────────┴────────────┐
                               ▼                          ▼
                         ┌───────────┐              ┌────────────────────┐
                         │ voto (db) │              │ voto_invalido (db) │
                         └───────────┘              └────────────────────┘
```

## Segurança

A API utiliza Spring Security + JWT.

Para efetuar testes nos endpoints protegidos, primeiramente deve-se cadastrar e logar como admin:

```text
POST /api/v1/admin/registrar
POST /api/v1/admin/logar
```

Após o login, o token deve ser enviado no *header* das requisições:

```http
Authorization: Bearer <token>
```

##  Testes

Foram implementados:

* **Testes unitários**
* **Testes de integração**

Principais ferramentas:

* JUnit
* Mockito
* H2

Executar os testes:

```bash
mvn test
```


## Rodando a Aplicação com Docker compose


Clone o projeto:

```bash
git clone <URL_DO_REPOSITORIO>
cd <DIRETORIO_DO_PROJETO>
```

Suba os serviços:

```bash
docker compose up -d
```

Verifique os containers:

```bash
docker compose ps
```

Para finalizar:

```bash
docker compose down
```

## Swagger

A documentação da API está disponível através do Swagger:

**http://localhost:8080/swagger-ui/index.html**

## Deploy AWS

A aplicação possui deploy automatizado na AWS utilizando Github Actions para integração e entrega contínua (CI/CD).

- **GitHub Actions:** [Acessar workflow](https://github.com/gustavohmoises/desafio-votacao/actions)
- **Aplicação na AWS:** [Acessar API](http://desafio-votacao-lb-948272438.us-east-2.elb.amazonaws.com/swagger-ui/index.html)

> ¹Por se tratar de um projeto de demonstração, a infraestrutura AWS pode estar temporariamente pausada para controle de custos.

### Serviços utilizados
- **ECR** - Armazenamento das imagens Docker
- **ECS + Fargate** - Execução dos containers em modo serverless
- **RDS** - PostgreSQL
- **ElastiCache** - Redis
- **MSK** - Kafka
- **Load Balancer** - Distribuição de tráfego

## Melhorias Futuras

Algumas melhorias podem ser implementadas futuramente para aumentar a escalabilidade, segurança e eficiência da aplicação:

- Paginação dos resultados
- Login de associados e perfis de usuários
- Endpoints de edição de pautas e associados
- Filtros e ordenação das consultas
- *Soft delete* para as entidades
- Persistência dos totalizadores de votos
- Retornar ID de processamento do voto para consulta do cliente