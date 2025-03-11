package services.aceitacao;


import br.com.cadastro.alunos.CadastroAlunosApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Tag;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.ComponentScan;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@Tag("aceitacao")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = CadastroAlunosApplication.class) // Especifique a classe de configuração
@ComponentScan(basePackages = "services")
public class CadastroAlunoStepsTest {
    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "http://localhost:8080";
        RestAssured.basePath = "/v1/alunos";
    }

    @Test
    public void testCadastrarAlunoComSucesso() {
        // Corpo da requisição
        String alunoJson = "{ \"cpf\": \"123.456.789-00\", \"nome\": \"João da Silva Souza\", \"endereco\": \"Rua Avelar, número 34, casa 02, Bairro Exemplo\", \"turma\": \"1001B\", \"nota1\": 8.0, \"nota2\": 7.5, \"nota3\": 9.0 }";

        // Log da requisição
        log.info("Enviando requisição POST para /v1/alunos com o seguinte corpo:");
        log.info(alunoJson);

        // Envia a requisição
        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(alunoJson)
                .post();

        // Log da resposta
        log.info("Resposta recebida:");
        log.info("Status Code: {}", response.getStatusCode());
        log.info("Corpo da Resposta: {}", response.getBody().asString());

        // Verificações
        assertEquals(201, response.getStatusCode(), "O status da resposta deve ser 201 (Created)");
        assertTrue(response.getBody().asString().contains("João da Silva Souza"), "A resposta deve conter o nome do aluno");
    }

    @Test
    public void testCadastrarAlunoComCPFInvalido() {
        // Corpo da requisição
        String alunoJson = "{ \"cpf\": \"123\", \"nome\": \"João da Silva Souza\", \"endereco\": \"Rua Avelar, número 34, casa 02, Bairro Exemplo\", \"turma\": \"1001B\", \"nota1\": 8.0, \"nota2\": 7.5, \"nota3\": 9.0 }";

        // Log da requisição
        log.info("Enviando requisição POST para /v1/alunos com CPF inválido:");
        log.info(alunoJson);

        // Envia a requisição
        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(alunoJson)
                .post();

        // Log da resposta
        log.info("Resposta recebida:");
        log.info("Status Code: {}", response.getStatusCode());
        log.info("Corpo da Resposta: {}", response.getBody().asString());

        // Verificações
        assertEquals(400, response.getStatusCode(), "O status da resposta deve ser 400 (Bad Request)");
        assertTrue(response.getBody().asString().contains("O CPF do aluno não é válido"), "A resposta deve conter a mensagem de erro esperada");
    }
}