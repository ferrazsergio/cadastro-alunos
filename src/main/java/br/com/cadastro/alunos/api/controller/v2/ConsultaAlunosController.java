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

@RestController
@RequestMapping("/v2/alunos")
@Tag(name = "Consulta de alunos", description = "Endpoints para consulta de informações de alunos")
@Validated
@SuppressWarnings("java:S1192")
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

        if ("uma-prova".equals(tipo) && "aprovado".equals(situacao)) {
            //não pode haver aluno aprovado que fez apenas uma prova
            return ResponseEntity.badRequest().build(); // Retorna 400 Bad Request
        }

        if ("uma-prova".equals(tipo)) {
            // Se tipo=uma-prova está presente (sozinho ou com situacao=reprovado)
            alunos = consultaAlunoService.listarAlunosReprovadosUmaProva();
        } else if ("aprovado".equals(situacao)) {
            // Se situacao=aprovado (e tipo não é uma-prova)
            alunos = consultaAlunoService.listarAlunosAprovados();
        } else if ("reprovado".equals(situacao)) {
            // Se situacao=reprovado (e tipo não é uma-prova)
            alunos = consultaAlunoService.listarTodosAlunosReprovados();
        } else {
            alunos = consultaAlunoService.listarTodosAlunos();
        }

        if (alunos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<AlunoDTO> alunosDTO = alunos.stream()
                .map(alunoMapper::toDTO)
                .toList();

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

            @Parameter(description = "Situação do aluno: aprovado, reprovado, todos")
            @RequestParam(required = false, defaultValue = "todos") String situacao,

            @Parameter(description = "Número da página (começando em 0)")
            @RequestParam(defaultValue = "0") @Min(0) int page,

            @Parameter(description = "Tamanho da página")
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {

        Page<Aluno> alunos;

        // Lógica de seleção do tipo de consulta
        if ("aprovado".equalsIgnoreCase(situacao)) {
            alunos = consultaAlunoService.buscarAlunosAprovadosPorTurma(turma, page, size);
        } else if ("reprovado".equalsIgnoreCase(situacao)) {
            alunos = consultaAlunoService.buscarAlunosReprovadosPorTurma(turma, page, size);
        } else {
            alunos = consultaAlunoService.buscarTodosAlunosPorTurma(turma, page, size);
        }

        Page<AlunoDTO> alunosDTO = alunos.map(alunoMapper::toDTO);
        return ResponseEntity.ok(alunosDTO);
    }

}