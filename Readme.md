# Cadastro de Alunos API

Este é um projeto de API para cadastro e verificação de alunos, desenvolvido para fins de estudo e suporte ao TCC em Ciência da Computação com foco em testes de software.

## Tecnologias Utilizadas

- Java
- Spring Boot
- Spring Data JPA
- H2 Database
- Swagger (OpenAPI)
- Lombok
- Log4j
- Maven

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
- **Método:** GET
- **URL:** `/v1/alunos`

#### Incluir Aluno
- **Método:** POST
- **URL:** `/v1/alunos`
- **Validações:**  
  - CPF obrigatório e único.
  - Nome, endereço e turma obrigatórios.
  - Notas devem ser entre 0 e 10.

#### Excluir Aluno
- **Método:** DELETE
- **URL:** `/v1/alunos/{cpf}`

#### Alterar Aluno
- **Método:** PUT
- **URL:** `/v1/alunos`

### Versão 2

#### Listar Alunos Aprovados
- **Método:** GET
- **URL:** `/v2/alunos/aprovados`
- **Critério:** Média das notas maior que 7.

#### Listar Alunos Reprovados em Uma Prova
- **Método:** GET
- **URL:** `/v2/alunos/reprovados`
- **Critério:** Apenas uma nota registrada.

#### Buscar Alunos Aprovados por Turma
- **Método:** GET
- **URL:** `/v2/alunos/aprovados/por-turma/turma=1001B&pageNumber=0&pageSize=10`

## Configuração

A aplicação utiliza um banco de dados H2 em memória por padrão. Para acessar o console do H2, utilize o seguinte URL:  
[http://localhost:8080/h2-ui](http://localhost:8080/h2-ui)

## Documentação da API

A documentação da API pode ser acessada através do Swagger UI:  
[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

## Requisitos Implementados

- **Entidade `Aluno` com os atributos:**  
  - `cpf` (chave primária), `nome`, `endereço`, `turma`, `nota1`, `nota2`, `nota3`
- **Operações:**
  - Incluir, excluir, alterar e listar alunos.
- **Serviços:**
  - Listar alunos aprovados com média maior que 7.
  - Listar alunos reprovados com apenas uma prova registrada.
- **Validações:**
  - CPF único e obrigatório.
  - Notas entre 0 e 10.
  - Cadastro em uma única turma por aluno.
- **Outras Tecnologias:**
  - Utilização de OpenAPI para documentação.
  - Log das operações utilizando Log4j.
  - Lombok para geração de código boilerplate.
  - Maven para gerenciamento das dependências.

## Execução

Para executar a aplicação, basta executar a classe principal `CadastroAlunosApplication`.