# Cadastro de Alunos API

Este é um projeto de API para cadastro e verificação de alunos.

## Tecnologias Utilizadas

- Java
- Spring Boot
- Spring Data JPA
- H2 Database
- Swagger
- Lombok

## Estrutura do Projeto

O projeto está dividido em pacotes de acordo com as versões da API:

- `br.com.cadastro.alunos.api.controller.v1`: Contém os controladores da versão 1 da API.
- `br.com.cadastro.alunos.api.controller.v2`: Contém os controladores da versão 2 da API.
- `br.com.cadastro.alunos.config`: Configurações da aplicação, incluindo as interfaces Swagger.
- `br.com.cadastro.alunos.model.entities`: Entidades do modelo de dados.
- `br.com.cadastro.alunos.model.repository`: Repositórios JPA para acesso ao banco de dados.
- `br.com.cadastro.alunos.model.services`: Serviços para manipulação de dados e lógica de negócio.

## Endpoints

### Versão 1

#### Listar Alunos
- Método: GET
- URL: /v1/alunos

#### Incluir Aluno
- Método: POST
- URL: /v1/alunos

#### Excluir Aluno
- Método: DELETE
- URL: /v1/alunos/{cpf}

#### Alterar Aluno
- Método: PUT
- URL: /v1/alunos

### Versão 2

#### Listar Alunos Aprovados
- Método: GET
- URL: /v2/alunos/aprovados

#### Listar Alunos Reprovados em Uma Prova
- Método: GET
- URL: /v2/alunos/reprovados

#### Buscar Alunos Aprovados por Turma
- Método: GET
- URL: /v2/alunos/aprovados/turma=1001B&pageNumber=0&pageSize=10

## Configuração

A aplicação utiliza um banco de dados H2 em memória por padrão. Para acessar o console do H2, utilize o seguinte URL: 

http://localhost:8080/h2-ui

## Documentação da API

A documentação da API pode ser acessada através do Swagger UI:

http://localhost:8080/swagger-ui.html

## Execução

Para executar a aplicação, basta executar a classe principal `CadastroAlunosApplication`.

## Observações

- O projeto utiliza o Lombok para geração automática de getters, setters e construtores.
- As operações sobre os alunos são registradas em logs.
- A média das notas dos alunos é utilizada para determinar se estão aprovados ou reprovados.