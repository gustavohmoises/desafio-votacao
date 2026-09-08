# API de Votação

API REST para gerenciamento de associados, pautas e votações.

## Tecnologias Principais

* Java 21
* Spring Boot 4.1.1
* Maven
* PostgreSQL 16 / H2 (testes)
* Docker
* Flyway
* Redis

## Como Rodar a Aplicação

```bash
docker compose up -d
```

## Documentação da API

A documentação dos endpoints está disponível através do Swagger:

**[Swagger UI](http://localhost:8080/swagger-ui/index.html)**

## Cache - Endpoint de Votação

Considerando a possibilidade de o endpoint de votação receber um alto volume de requisições, foi implementado o uso do Redis como mecanismo de cache a fim de reduzir consultas ao banco de dados.

## Melhorias Futuras

Algumas melhorias podem ser implementadas futuramente para aumentar a escalabilidade, segurança e eficiência da aplicação:

* Paginação dos resultados
* Autenticação com Spring Security
* Pipelines de CI/CD para automatizar build, testes e deploy