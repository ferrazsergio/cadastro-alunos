package br.com.cadastro.alunos.config;

import br.com.cadastro.alunos.model.entities.Aluno;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface SwaggerConfigV1 {

    @Operation(summary = "Lista todos os alunos")
    ResponseEntity<List<Aluno>> listarAlunos();

    @Operation(summary = "Inclui um novo aluno")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aluno cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida")
    })
    ResponseEntity<?> incluirAluno(Aluno aluno);

    @Operation(summary = "Exclui um aluno pelo CPF")
    ResponseEntity<Void> excluirAluno(
            @Parameter(description = "CPF do aluno a ser excluído", required = true) String cpf);

    @Operation(summary = "Altera dados de um aluno pelo CPF")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dados do aluno alterado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida")
    })
    ResponseEntity<String> alterarAluno(
            @Parameter(description = "CPF do aluno a ser alterado", required = true) String cpf,
            @Parameter(description = "Novos dados do aluno", required = true) Aluno aluno);


}




