package br.com.cadastro.alunos.api.controller.v1;

import br.com.cadastro.alunos.model.dto.AlunoDTO;
import br.com.cadastro.alunos.model.entities.Aluno;
import br.com.cadastro.alunos.model.mapper.AlunoMapper;
import br.com.cadastro.alunos.model.services.AlunoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Tag(name = "Cadastro de Alunos", description = "Endpoints para operações CRUD de alunos")
@RequestMapping("v1/alunos")
public class CadastroAlunosController {

    private final AlunoService alunoService;
    private final AlunoMapper alunoMapper;

    @Autowired
    public CadastroAlunosController(AlunoService alunoService, AlunoMapper alunoMapper) {
        this.alunoService = alunoService;
        this.alunoMapper = alunoMapper;
    }

    @GetMapping()
    @Operation(summary = "Listar todos os alunos", description = "Retorna uma lista com todos os alunos cadastrados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de alunos retornada com sucesso",
                    content = @Content(schema = @Schema(implementation = AlunoDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<List<AlunoDTO>> listarAlunos() {
        List<Aluno> alunos = alunoService.listarAlunos();
        List<AlunoDTO> alunosDTO = alunos.stream()
                .map(alunoMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(alunosDTO);
    }

    @PostMapping
    @Operation(summary = "Cadastrar um aluno", description = "Cadastra um novo aluno no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Aluno cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<AlunoDTO> incluirAluno(
            @Parameter(description = "Dados do aluno a ser cadastrado", required = true)
            @Valid @RequestBody Aluno aluno) {

        Aluno alunoSalvo = alunoService.incluirAluno(aluno);
        AlunoDTO alunoDTO = alunoMapper.toDTO(alunoSalvo);
        return ResponseEntity.status(HttpStatus.CREATED).body(alunoDTO);
    }

    @DeleteMapping("{cpf}")
    @Operation(summary = "Excluir um aluno", description = "Remove um aluno do sistema pelo CPF")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Aluno excluído com sucesso"),
            @ApiResponse(responseCode = "404", description = "Aluno não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<Void> excluirAluno(
            @Parameter(description = "CPF do aluno a ser excluído", required = true)
            @Valid @PathVariable String cpf) {

        alunoService.excluirAluno(cpf);
        return ResponseEntity.noContent().build();
    }

    @PutMapping
    @Operation(summary = "Alterar um aluno", description = "Atualiza os dados de um aluno existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aluno alterado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Aluno não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<AlunoDTO> alterarAluno(
            @Parameter(description = "Dados do aluno a ser alterado", required = true)
            @Valid @RequestBody Aluno aluno) {

        Aluno alunoAlterado = alunoService.alterarAluno(aluno.getCpf(), aluno);
        AlunoDTO alunoDTO = alunoMapper.toDTO(alunoAlterado);
        return ResponseEntity.ok(alunoDTO);
    }
}