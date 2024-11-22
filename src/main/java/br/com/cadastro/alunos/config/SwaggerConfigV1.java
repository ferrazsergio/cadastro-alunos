package br.com.cadastro.alunos.config;

import br.com.cadastro.alunos.model.entities.Aluno;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aluno deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Aluno não encontrado")
    })

    ResponseEntity<Void> excluirAluno(
            @Parameter(description = "CPF do aluno a ser excluído", required = true) String cpf);


    @Operation(summary = "Altera dados de um aluno")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dados do aluno alterados com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida"),
            @ApiResponse(responseCode = "404", description = "Aluno não encontrado")
    })
    @PutMapping
    ResponseEntity<String> alterarAluno(@Valid @RequestBody Aluno aluno);
}




