package br.com.cadastro.alunos.api.controller.v2;

import br.com.cadastro.alunos.model.dto.AlunoDTO;
import br.com.cadastro.alunos.model.entities.Aluno;
import br.com.cadastro.alunos.model.mapper.AlunoMapper;
import br.com.cadastro.alunos.model.services.ConsultaAlunoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v2/alunos")
@Tag(name = "Consulta de alunos", description = "Endpoints para consulta de informações de alunos")
@Validated
public class ConsultaAlunosController {

    private final ConsultaAlunoService consultaAlunoService;
    private final AlunoMapper alunoMapper;

    @Autowired
    public ConsultaAlunosController(ConsultaAlunoService consultaAlunoService, AlunoMapper alunoMapper) {
        this.consultaAlunoService = consultaAlunoService;
        this.alunoMapper = alunoMapper;
    }

    @GetMapping
    @Operation(summary = "Listar alunos", description = "Retorna lista de alunos com filtros opcionais")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Alunos encontrados com sucesso"),
            @ApiResponse(responseCode = "204", description = "Nenhum aluno encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<List<AlunoDTO>> listarAlunos(
            @Parameter(description = "Situação do aluno: aprovado, reprovado")
            @RequestParam(required = false) String situacao,

            @Parameter(description = "Tipo de reprovação: uma-prova")
            @RequestParam(required = false) String tipo) {

        List<Aluno> alunos;

        if ("aprovado".equals(situacao)) {
            alunos = consultaAlunoService.listarAlunosAprovados();
        } else if ("reprovado".equals(situacao)) {
            if ("uma-prova".equals(tipo)) {
                alunos = consultaAlunoService.listarAlunosReprovadosUmaProva();
            } else {
                alunos = consultaAlunoService.listarTodosAlunosReprovados();
            }
        } else {
            alunos = consultaAlunoService.listarTodosAlunos();
        }

        if (alunos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<AlunoDTO> alunosDTO = alunos.stream()
                .map(alunoMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(alunosDTO);
    }

    @GetMapping("/por-turma")
    @Operation(summary = "Buscar alunos por turma", description = "Retorna alunos de uma turma específica com filtros")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Alunos encontrados com sucesso"),
            @ApiResponse(responseCode = "404", description = "Nenhum aluno encontrado na turma informada"),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Page<AlunoDTO>> buscarAlunosPorTurma(
            @Parameter(description = "Código da turma", required = true)
            @RequestParam @NotBlank String turma,

            @Parameter(description = "Situação do aluno: aprovado, reprovado")
            @RequestParam(required = false, defaultValue = "aprovado") String situacao,

            @Parameter(description = "Número da página (começando em 0)")
            @RequestParam(defaultValue = "0") @Min(0) int page,

            @Parameter(description = "Tamanho da página")
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {

        Page<Aluno> alunos;

        if ("aprovado".equals(situacao)) {
            alunos = consultaAlunoService.buscarAlunosAprovadosPorTurma(turma, page, size);
        } else if ("reprovado".equals(situacao)) {
            alunos = consultaAlunoService.buscarAlunosReprovadosPorTurma(turma, page, size);
        } else {
            alunos = consultaAlunoService.buscarTodosAlunosPorTurma(turma, page, size);
        }

        Page<AlunoDTO> alunosDTO = alunos.map(alunoMapper::toDTO);
        return ResponseEntity.ok(alunosDTO);
    }

    // Mantém os endpoints existentes por compatibilidade, mas marca como deprecated

    @Deprecated
    @GetMapping("/aprovados")
    @Operation(summary = "Listar alunos aprovados", description = "Retorna todos os alunos aprovados", deprecated = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Alunos aprovados encontrados"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<List<AlunoDTO>> listarAlunosAprovados() {
        List<Aluno> alunos = consultaAlunoService.listarAlunosAprovados();
        List<AlunoDTO> alunosDTO = alunos.stream()
                .map(alunoMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(alunosDTO);
    }

    @Deprecated
    @GetMapping("/reprovados")
    @Operation(summary = "Listar alunos reprovados", description = "Retorna alunos reprovados com filtro opcional", deprecated = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Alunos reprovados encontrados"),
            @ApiResponse(responseCode = "204", description = "Nenhum aluno reprovado encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<List<AlunoDTO>> listarAlunosReprovados(
            @Parameter(description = "Tipo de reprovação: uma-prova para reprovados em apenas uma prova")
            @RequestParam(value = "tipo", required = false) String tipo) {

        List<Aluno> alunos;
        if ("uma-prova".equals(tipo)) {
            alunos = consultaAlunoService.listarAlunosReprovadosUmaProva();
        } else {
            alunos = consultaAlunoService.listarTodosAlunosReprovados();
        }

        if (alunos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<AlunoDTO> alunosDTO = alunos.stream()
                .map(alunoMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(alunosDTO);
    }

    @Deprecated
    @GetMapping("/aprovados-por-turma")
    @Operation(summary = "Buscar alunos aprovados por turma", description = "Retorna alunos aprovados de uma turma específica", deprecated = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Alunos aprovados encontrados"),
            @ApiResponse(responseCode = "404", description = "Nenhum aluno aprovado encontrado na turma"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Page<AlunoDTO>> buscarAlunosAprovadosPorTurma(
            @Parameter(description = "Código da turma", required = true)
            @RequestParam String turma,
            @Parameter(description = "Número da página")
            @RequestParam(defaultValue = "0") int pageNumber,
            @Parameter(description = "Tamanho da página")
            @RequestParam(defaultValue = "10") int pageSize) {

        Page<Aluno> alunos = consultaAlunoService.buscarAlunosAprovadosPorTurma(turma, pageNumber, pageSize);
        Page<AlunoDTO> alunosDTO = alunos.map(alunoMapper::toDTO);
        return ResponseEntity.ok(alunosDTO);
    }

    @Deprecated
    @GetMapping("/reprovados-por-turma")
    @Operation(summary = "Buscar alunos reprovados por turma", description = "Retorna alunos reprovados de uma turma específica", deprecated = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Alunos reprovados encontrados"),
            @ApiResponse(responseCode = "404", description = "Nenhum aluno reprovado encontrado na turma"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Page<AlunoDTO>> buscarAlunosReprovadosPorTurma(
            @Parameter(description = "Código da turma", required = true)
            @RequestParam String turma,
            @Parameter(description = "Número da página")
            @RequestParam(defaultValue = "0") int pageNumber,
            @Parameter(description = "Tamanho da página")
            @RequestParam(defaultValue = "10") int pageSize) {

        Page<Aluno> alunos = consultaAlunoService.buscarAlunosReprovadosPorTurma(turma, pageNumber, pageSize);
        Page<AlunoDTO> alunosDTO = alunos.map(alunoMapper::toDTO);
        return ResponseEntity.ok(alunosDTO);
    }
}