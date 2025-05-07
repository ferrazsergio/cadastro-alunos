package br.com.cadastro.alunos.config;

import br.com.cadastro.alunos.model.entities.Aluno;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface SwaggerConfigV2 {

    @Operation(summary = "Lista todos os alunos aprovados")
    ResponseEntity<List<Aluno>> listarAlunosAprovados();

    @Operation(summary = "Lista todos os alunos reprovados que só fizeram uma prova")
	ResponseEntity<List<Aluno>> listarAlunosReprovados(String tipo);

    @Operation(summary = "Busca alunos aprovados por turma")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Alunos aprovados encontrados com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida")
    })

    ResponseEntity<Page<Aluno>> buscarAlunosAprovadosPorTurma(
            @Parameter(description = "Número da turma", required = true) String turma,
            @Parameter(description = "Número da página") int pageNumber,
            @Parameter(description = "Tamanho da página") int pageSize);


    @Operation(summary = "Busca alunos reprovados por turma")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Alunos reprovados encontrados com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida")
    })

    ResponseEntity<Page<Aluno>> buscarAlunosReprovadosPorTurma(
            @Parameter(description = "Número da turma", required = true) String turma,
            @Parameter(description = "Número da página") int pageNumber,
            @Parameter(description = "Tamanho da página") int pageSize);
}