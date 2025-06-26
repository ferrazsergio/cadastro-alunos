package services.aceitacao;

import br.com.cadastro.alunos.CadastroAlunosApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.springframework.boot.test.context.SpringBootTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.ComponentScan;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@Tag("aceitacao")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = CadastroAlunosApplication.class)
@ComponentScan(basePackages = "services")
class CadastroAlunoStepsTest {

    @BeforeAll
    static void setup() {
        log.info("=== Iniciando testes de aceitação do CadastroAlunoSteps ===");
        log.info("🧪 Executado por: ferrazsergio em 2025-06-26 18:20:32");
        log.info("🌐 Configurando RestAssured para testes E2E...");

        RestAssured.baseURI = "http://localhost:8080";
        RestAssured.basePath = "/v1/alunos";

        log.info("✅ RestAssured configurado:");
        log.info("  - Base URI: {}", RestAssured.baseURI);
        log.info("  - Base Path: {}", RestAssured.basePath);
        log.info("  - URL completa: {}{}", RestAssured.baseURI, RestAssured.basePath);
        log.info("🚀 Iniciando Spring Boot em modo DEFINED_PORT para testes E2E");
    }

    @BeforeEach
    void beforeEach() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
        log.info("⏰ [{}] Iniciando novo teste de aceitação", timestamp);
    }

    @Test
    void testCadastrarAlunoComSucesso() {
        log.info("🧪 Testando cadastro de aluno com sucesso (E2E)");
        log.info("📋 Cenário: Cadastrar aluno com dados válidos deve retornar 201 Created");

        // Arrange
        String cpf = "123.456.789-00";
        String alunoJson = "{ \"cpf\": \"" + cpf + "\", \"nome\": \"João da Silva Souza\", " +
                "\"endereco\": \"Rua Avelar, número 34, casa 02, Bairro Exemplo\", " +
                "\"turma\": \"1001B\", \"nota1\": 8.0, \"nota2\": 7.5, \"nota3\": 9.0 }";

        log.debug("📝 Dados do aluno para cadastro:");
        log.debug("  - CPF: {}", cpf);
        log.debug("  - Nome: João da Silva Souza");
        log.debug("  - Endereço: Rua Avelar, número 34, casa 02, Bairro Exemplo");
        log.debug("  - Turma: 1001B");
        log.debug("  - Notas: [8.0, 7.5, 9.0] -> Média: {:.2f}", (8.0 + 7.5 + 9.0) / 3);

        log.info("📤 Enviando requisição POST para /v1/alunos");
        log.debug("🔗 URL completa: {}/v1/alunos", RestAssured.baseURI);
        log.debug("📄 Payload JSON: {}", alunoJson);

        // Act
        long startTime = System.currentTimeMillis();
        Response response = given()
                .contentType(ContentType.JSON)
                .body(alunoJson)
                .post();
        long endTime = System.currentTimeMillis();

        // Assert & Log
        log.info("📥 Resposta recebida em {}ms:", (endTime - startTime));
        log.info("  - Status Code: {}", response.getStatusCode());
        log.info("  - Content-Type: {}", response.getContentType());
        log.debug("  - Headers: {}", response.getHeaders());
        log.info("  - Body: {}", response.getBody().asString());

        log.info("✅ Verificando se cadastro foi bem-sucedido...");
        assertEquals(201, response.getStatusCode(), "O status da resposta deve ser 201 (Created)");
        log.debug("✓ Status 201 confirmado");

        assertTrue(response.getBody().asString().contains("João da Silva Souza"),
                "A resposta deve conter o nome do aluno");
        log.debug("✓ Nome do aluno encontrado na resposta");

        log.info("✅ Teste passou! Aluno '{}' cadastrado com sucesso", cpf);

        // Cleanup
        log.debug("🧹 Limpando dados de teste...");
        try {
            given().pathParam("cpf", cpf).delete("/{cpf}");
            log.debug("✅ Aluno removido para limpeza");
        } catch (Exception e) {
            log.warn("⚠️ Não foi possível remover aluno de teste: {}", e.getMessage());
        }
    }

    @Test
    void testCadastrarAlunoComCPFInvalido() {
        log.info("🧪 Testando cadastro de aluno com CPF inválido (deve falhar)");
        log.info("📋 Cenário: Cadastrar aluno com CPF inválido deve retornar 400 Bad Request");

        // Arrange
        String cpfInvalido = "123";
        String alunoJson = "{ \"cpf\": \"" + cpfInvalido + "\", \"nome\": \"João da Silva Souza\", " +
                "\"endereco\": \"Rua Avelar, número 34, casa 02, Bairro Exemplo\", " +
                "\"turma\": \"1001B\", \"nota1\": 8.0, \"nota2\": 7.5, \"nota3\": 9.0 }";

        log.debug("❌ Dados inválidos para teste:");
        log.debug("  - CPF inválido: '{}' (tamanho: {}, esperado: 14)", cpfInvalido, cpfInvalido.length());
        log.debug("  - Nome: João da Silva Souza");
        log.debug("  - Outros dados: válidos");

        log.info("📤 Enviando requisição POST com CPF inválido");
        log.debug("📄 Payload JSON: {}", alunoJson);

        // Act
        long startTime = System.currentTimeMillis();
        Response response = given()
                .contentType(ContentType.JSON)
                .body(alunoJson)
                .post();
        long endTime = System.currentTimeMillis();

        // Assert & Log
        log.info("📥 Resposta recebida em {}ms:", (endTime - startTime));
        log.info("  - Status Code: {}", response.getStatusCode());
        log.info("  - Body: {}", response.getBody().asString());

        log.info("✅ Verificando se erro foi tratado corretamente...");
        assertEquals(400, response.getStatusCode(), "O status da resposta deve ser 400 (Bad Request)");
        log.debug("✓ Status 400 confirmado");

        assertTrue(response.getBody().asString().contains("error") ||
                        response.getBody().asString().contains("mensagens") ||
                        response.getBody().asString().contains("CPF"),
                "A resposta deve conter informações sobre o erro");
        log.debug("✓ Mensagem de erro encontrada na resposta");

        log.info("✅ Teste passou! CPF inválido '{}' gerou erro 400 como esperado", cpfInvalido);
    }

    @Test
    void testListarAlunos() {
        log.info("🧪 Testando listagem de alunos");
        log.info("📋 Cenário: Listar todos os alunos deve retornar 200 OK com array JSON");

        log.info("📤 Enviando requisição GET para /v1/alunos");

        // Act
        long startTime = System.currentTimeMillis();
        Response response = given()
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract().response();
        long endTime = System.currentTimeMillis();

        // Assert & Log
        log.info("📥 Resposta recebida em {}ms:", (endTime - startTime));
        log.info("  - Status Code: {}", response.getStatusCode());

        String responseBody = response.getBody().asString();
        log.info("  - Tamanho da resposta: {} caracteres", responseBody.length());

        // Verificar se é uma array JSON válida
        if (responseBody.trim().equals("[]")) {
            log.info("  - Lista vazia: []");
        } else {
            log.info("  - Lista com dados: {}", responseBody.length() > 100 ?
                    responseBody.substring(0, 100) + "..." : responseBody);
        }

        log.info("✅ Verificando formato da resposta...");
        assertTrue(responseBody.startsWith("[") && responseBody.endsWith("]"),
                "A resposta deve ser uma lista JSON");
        log.debug("✓ Formato de array JSON confirmado");

        log.info("✅ Teste passou! Listagem retornou array JSON válido");
    }

    @Test
    void testDeletarAlunoComSucesso() {
        log.info("🧪 Testando exclusão de aluno com sucesso");
        log.info("📋 Cenário: Cadastrar aluno → Excluir aluno → Verificar se foi removido");

        // Arrange
        String cpf = "111.222.333-44";
        String alunoJson = "{ \"cpf\": \"" + cpf + "\", \"nome\": \"Aluno Teste Exclusão\", " +
                "\"endereco\": \"Rua da Exclusão, número 404, Bairro Teste, Cidade Teste\", " +
                "\"turma\": \"1003C\", \"nota1\": 6.0, \"nota2\": 7.0, \"nota3\": 5.5 }";

        log.debug("📝 Preparando aluno para exclusão:");
        log.debug("  - CPF: {}", cpf);
        log.debug("  - Nome: Aluno Teste Exclusão");
        log.debug("  - Notas: [6.0, 7.0, 5.5] -> Média: {:.2f}", (6.0 + 7.0 + 5.5) / 3);

        // Step 1: Cadastrar aluno
        log.info("📤 PASSO 1: Cadastrando aluno para posterior exclusão...");
        Response cadastroResponse = given()
                .contentType(ContentType.JSON)
                .body(alunoJson)
                .post();

        log.debug("  - Status cadastro: {}", cadastroResponse.getStatusCode());
        log.debug("  - Resposta cadastro: {}", cadastroResponse.getBody().asString());

        // Step 2: Excluir aluno
        log.info("📤 PASSO 2: Excluindo aluno com CPF: {}", cpf);
        long startTime = System.currentTimeMillis();
        Response response = given()
                .pathParam("cpf", cpf)
                .delete("/{cpf}");
        long endTime = System.currentTimeMillis();

        log.info("📥 Resposta da exclusão recebida em {}ms:", (endTime - startTime));
        log.info("  - Status Code: {}", response.getStatusCode());
        log.info("  - Body: {}", response.getBody().asString());

        // Step 3: Verificar exclusão
        log.info("✅ PASSO 3: Verificando se exclusão foi bem-sucedida...");
        assertTrue(response.getStatusCode() >= 200 && response.getStatusCode() < 300,
                "O status da resposta deve ser de sucesso (2xx)");
        log.debug("✓ Status de sucesso confirmado: {}", response.getStatusCode());

        // Step 4: Verificar se aluno não está mais na lista
        log.info("🔍 PASSO 4: Verificando se aluno foi removido da lista...");
        String listaAlunos = given()
                .when()
                .get()
                .then()
                .extract().body().asString();

        boolean alunoAindaExiste = listaAlunos.contains(cpf);
        assertTrue(!alunoAindaExiste,
                "O aluno excluído não deve estar na lista de todos os alunos");

        if (alunoAindaExiste) {
            log.warn("❌ Aluno ainda encontrado na lista após exclusão!");
        } else {
            log.debug("✓ Aluno não encontrado na lista (exclusão confirmada)");
        }

        log.info("✅ Teste passou! Aluno '{}' excluído com sucesso", cpf);
    }

    @Test
    void testExcluirAlunoNaoExistente() {
        log.info("🧪 Testando exclusão de aluno não existente (deve falhar)");
        log.info("📋 Cenário: Tentar excluir aluno inexistente deve retornar erro");

        // Arrange
        String cpfInexistente = "999.999.999-99";

        log.debug("❌ Dados para teste de falha:");
        log.debug("  - CPF inexistente: {}", cpfInexistente);

        log.info("📤 Enviando requisição DELETE para CPF inexistente: {}", cpfInexistente);

        // Act
        long startTime = System.currentTimeMillis();
        Response response = given()
                .pathParam("cpf", cpfInexistente)
                .delete("/{cpf}");
        long endTime = System.currentTimeMillis();

        // Assert & Log
        log.info("📥 Resposta recebida em {}ms:", (endTime - startTime));
        log.info("  - Status Code: {}", response.getStatusCode());
        log.info("  - Body: {}", response.getBody().asString());

        log.info("✅ Verificando se erro foi tratado corretamente...");
        assertTrue(response.getStatusCode() < 200 || response.getStatusCode() >= 300,
                "O status da resposta não deve ser de sucesso quando tentar excluir aluno inexistente");

        log.debug("✓ Status de erro confirmado: {} (não é 2xx)", response.getStatusCode());

        log.info("✅ Teste passou! Tentativa de excluir CPF inexistente '{}' gerou erro como esperado", cpfInexistente);
    }

    @Test
    void testAlterarAlunoComSucesso() {
        log.info("🧪 Testando alteração de aluno com sucesso");
        log.info("📋 Cenário: Cadastrar aluno → Alterar dados → Verificar alteração");

        // Arrange
        String cpf = "987.654.321-00";
        String nomeOriginal = "Maria Silva";
        String nomeAlterado = "Maria Silva Oliveira";

        String alunoJson = "{ \"cpf\": \"" + cpf + "\", \"nome\": \"" + nomeOriginal + "\", " +
                "\"endereco\": \"Avenida Principal, número 100, Apartamento 202, Centro, Cidade Nova\", " +
                "\"turma\": \"1002A\", \"nota1\": 7.0, \"nota2\": 6.5, \"nota3\": 8.0 }";

        log.debug("📝 Preparando aluno para alteração:");
        log.debug("  - CPF: {}", cpf);
        log.debug("  - Nome original: {}", nomeOriginal);
        log.debug("  - Nome que será alterado para: {}", nomeAlterado);
        log.debug("  - Notas originais: [7.0, 6.5, 8.0] -> Média: {:.2f}", (7.0 + 6.5 + 8.0) / 3);

        // Step 1: Cadastrar aluno
        log.info("📤 PASSO 1: Cadastrando aluno para posterior alteração...");
        Response cadastroResponse = given()
                .contentType(ContentType.JSON)
                .body(alunoJson)
                .post();

        log.debug("  - Status cadastro: {}", cadastroResponse.getStatusCode());
        log.debug("  - Resposta cadastro: {}", cadastroResponse.getBody().asString());

        // Step 2: Alterar aluno
        String alunoAlteradoJson = "{ \"cpf\": \"" + cpf + "\", \"nome\": \"" + nomeAlterado + "\", " +
                "\"endereco\": \"Avenida Principal, número 100, Apartamento 202, Centro, Cidade Nova\", " +
                "\"turma\": \"1002A\", \"nota1\": 9.0, \"nota2\": 8.5, \"nota3\": 9.2 }";

        log.info("📤 PASSO 2: Alterando dados do aluno...");
        log.debug("  - Novas notas: [9.0, 8.5, 9.2] -> Nova média: {:.2f}", (9.0 + 8.5 + 9.2) / 3);
        log.debug("📄 Payload de alteração: {}", alunoAlteradoJson);

        long startTime = System.currentTimeMillis();
        Response response = given()
                .contentType(ContentType.JSON)
                .body(alunoAlteradoJson)
                .put();
        long endTime = System.currentTimeMillis();

        // Assert & Log
        log.info("📥 Resposta da alteração recebida em {}ms:", (endTime - startTime));
        log.info("  - Status Code: {}", response.getStatusCode());
        log.info("  - Body: {}", response.getBody().asString());

        log.info("✅ PASSO 3: Verificando se alteração foi bem-sucedida...");
        assertEquals(200, response.getStatusCode(), "O status da resposta deve ser 200 (OK)");
        log.debug("✓ Status 200 confirmado");

        assertTrue(response.getBody().asString().contains(nomeAlterado),
                "A resposta deve conter o nome alterado do aluno");
        log.debug("✓ Nome alterado '{}' encontrado na resposta", nomeAlterado);

        log.info("✅ Teste passou! Aluno '{}' alterado com sucesso de '{}' para '{}'",
                cpf, nomeOriginal, nomeAlterado);

        // Cleanup
        log.debug("🧹 Limpando dados de teste...");
        try {
            given().pathParam("cpf", cpf).delete("/{cpf}");
            log.debug("✅ Aluno removido para limpeza");
        } catch (Exception e) {
            log.warn("⚠️ Não foi possível remover aluno de teste: {}", e.getMessage());
        }
    }
}