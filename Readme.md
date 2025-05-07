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

O projeto está organizado seguindo princípios de arquitetura em camadas:

- `br.com.cadastro.alunos.api.controller.v1`: Controladores da versão 1 da API (operações CRUD).
- `br.com.cadastro.alunos.api.controller.v2`: Controladores da versão 2 da API (consultas especializadas).
- `br.com.cadastro.alunos.api.exceptionhandler`: Manipuladores de exceção globais.
- `br.com.cadastro.alunos.config`: Configurações da aplicação, incluindo Swagger.
- `br.com.cadastro.alunos.model.dto`: DTOs (Data Transfer Objects) para transferência de dados.
- `br.com.cadastro.alunos.model.entities`: Entidades JPA do modelo de dados.
- `br.com.cadastro.alunos.model.exceptions`: Exceções customizadas da aplicação.
- `br.com.cadastro.alunos.model.mapper`: Mapeadores para conversão entre entidades e DTOs.
- `br.com.cadastro.alunos.model.repository`: Repositórios JPA para acesso ao banco de dados.
- `br.com.cadastro.alunos.model.services`: Serviços de negócio.

## Boas Práticas Implementadas

1. **Separação de Responsabilidades**:
  - Controllers para manipulação de requisições HTTP
  - Services para lógica de negócio
  - Repositories para acesso a dados

2. **Padrão DTO**:
  - Uso de DTOs para transferência de dados entre camadas
  - Proteção das entidades JPA da exposição direta na API

3. **Tratamento Centralizado de Exceções**:
  - `GlobalExceptionHandler` para tratamento consistente de erros
  - Exceções específicas de negócio (`BusinessException`, `ResourceNotFoundException`)

4. **Versionamento de API**:
  - API V1: Operações CRUD básicas
  - API V2: Consultas especializadas

5. **Documentação Completa**:
  - Anotações OpenAPI/Swagger para documentação de endpoints
  - Exemplos e descrições para melhor usabilidade da API

## Endpoints

### Versão 1 - Operações CRUD

#### Listar Alunos
- **Método:** GET
- **URL:** `/v1/alunos`
- **Resposta:** Lista de AlunoDTO com CPF, nome, turma, média e situação.

#### Incluir Aluno
- **Método:** POST
- **URL:** `/v1/alunos`
- **Corpo:** Objeto Aluno com CPF, nome, endereço, turma e notas.
- **Resposta:** AlunoDTO do aluno inserido.
- **Validações:**
  - CPF obrigatório e único (13 caracteres).
  - Nome entre 10 e 40 caracteres.
  - Endereço entre 25 e 100 caracteres.
  - Turma entre 4 e 5 caracteres.
  - Notas entre 0 e 10.

#### Excluir Aluno
- **Método:** DELETE
- **URL:** `/v1/alunos/{cpf}`
- **Resposta:** HTTP 204 (No Content).

#### Alterar Aluno
- **Método:** PUT
- **URL:** `/v1/alunos`
- **Corpo:** Objeto Aluno com os dados atualizados.
- **Resposta:** AlunoDTO do aluno alterado.

### Versão 2 - Consultas Especializadas

#### Listar Alunos (com filtros)
- **Método:** GET
- **URL:** `/v2/alunos`
- **Parâmetros:**
  - `situacao`: "aprovado" ou "reprovado" (opcional)
  - `tipo`: "uma-prova" (apenas para reprovados, opcional)
- **Resposta:** Lista de AlunoDTO filtrada conforme parâmetros.

#### Buscar Alunos por Turma (com filtros e paginação)
- **Método:** GET
- **URL:** `/v2/alunos/por-turma`
- **Parâmetros:**
  - `turma`: Código da turma (obrigatório)
  - `situacao`: "aprovado" ou "reprovado" (padrão: "aprovado")
  - `page`: Número da página (padrão: 0)
  - `size`: Tamanho da página (padrão: 10, máx: 100)
- **Resposta:** Página de AlunoDTO.

#### Endpoints Legados (Deprecated)
- GET `/v2/alunos/aprovados`: Lista alunos aprovados.
- GET `/v2/alunos/reprovados?tipo=uma-prova`: Lista alunos reprovados em uma prova.
- GET `/v2/alunos/aprovados-por-turma?turma=1001B&pageNumber=0&pageSize=10`: Busca alunos aprovados por turma.
- GET `/v2/alunos/reprovados-por-turma?turma=1001B&pageNumber=0&pageSize=10`: Busca alunos reprovados por turma.

## Modelo de Dados

### Entidade Aluno
- **cpf**: String (Chave primária)
- **nome**: String
- **endereco**: String
- **turma**: String
- **nota1**: Double
- **nota2**: Double
- **nota3**: Double
- **aprovado**: String ("SIM"/"NÃO")

### DTO AlunoDTO
- **cpf**: String
- **nome**: String
- **turma**: String
- **media**: Double (Média calculada com duas casas decimais)
- **situacao**: String ("APROVADO"/"REPROVADO")
- **nota1**: Double
- **nota2**: Double
- **nota3**: Double

## Configuração

### Banco de Dados
A aplicação utiliza um banco de dados H2 em memória. Para acessar o console:
- URL: [http://localhost:8080/h2-ui](http://localhost:8080/h2-ui)
- JDBC URL: jdbc:h2:mem:testdb
- Usuário: sa
- Senha: 

### Documentação da API
A documentação completa da API pode ser acessada via Swagger UI:
- URL: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

## Testes

O projeto inclui diferentes níveis de testes:

### Testes Unitários
- `AlunoServiceTest`: Testa os serviços CRUD.
- `ConsultaAlunoServiceTest`: Testa os serviços de consulta.
- `AlunoMapperTest`: Testa o mapeamento entre entidades e DTOs.

### Testes de Integração
- `AlunoRepositoryIntegrationTest`: Testa a integração com o banco de dados.

### Testes de Aceitação
- `CadastroAlunoStepsTest`: Testa os endpoints da API com RestAssured.

## Execução


## Regras de Negócio

1. **Cálculo de Aprovação**:
  - Aluno é aprovado se a média das três notas for maior ou igual a 7.0.

2. **Validação de CPF**:
  - CPF deve ter 13 caracteres no formato XXX.XXX.XXX-XX.
  - CPF é único no sistema.

3. **Validação de Notas**:
  - Notas devem estar entre 0 e 10.

4. **Turma**:
  - Um aluno só pode estar cadastrado em uma turma.

## Melhorias Implementadas

1. **Uso de DTOs**:
  - Proteção das entidades JPA
  - Formato padronizado de resposta

2. **Tratamento de Exceções**:
  - Respostas de erro padronizadas
  - Mensagens descritivas de erro

3. **API mais RESTful**:
  - Uso correto dos códigos de status HTTP
  - Endpoints com recursos bem definidos

4. **Consultas Otimizadas**:
  - Queries específicas em JPQL para evitar processamento em memória

5. **Validação Robusta**:
  - Validação de parâmetros com Bean Validation
  - Mensagens de erro detalhadas

6. **Formatação de Dados**:
  - Média formatada com duas casas decimais
  - Indicação clara da situação do aluno

### Requisitos
- Java 17 ou superior
- Maven 3.8 ou superior

Para executar a aplicação, basta executar a classe principal `CadastroAlunosApplication`.