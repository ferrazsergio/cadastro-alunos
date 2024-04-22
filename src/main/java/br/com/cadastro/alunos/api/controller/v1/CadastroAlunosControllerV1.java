package br.com.cadastro.alunos.api.controller.v1;

import br.com.cadastro.alunos.config.SwaggerConfigV1;
import br.com.cadastro.alunos.model.entities.Aluno;
import br.com.cadastro.alunos.model.services.AlunoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Cadastro alunos CRUD", description = "Implementando operações")
@RequestMapping("v1")
public class CadastroAlunosControllerV1 implements SwaggerConfigV1 {

    private final AlunoService alunoService;

    @Autowired
    public CadastroAlunosControllerV1(AlunoService alunoService) {
        this.alunoService = alunoService;
    }
    @Override
    @GetMapping("alunos/lista")
    public ResponseEntity<List<Aluno>> listarAlunos(){
        try {
            List<Aluno> alunos = alunoService.listarAlunos();
            return ResponseEntity.ok(alunos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Override
    @PostMapping("alunos/inclusao")
    public ResponseEntity<String> incluirAluno(@Valid @RequestBody Aluno aluno) {
        try {
            alunoService.incluirAluno(aluno);
            return ResponseEntity.ok("Aluno cadastrado com sucesso");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao cadastrar aluno");
        }
    }

    @Override
    @DeleteMapping("alunos/exclusao/{cpf}")
    public ResponseEntity<Void> excluirAluno(@Valid @PathVariable String cpf) {
        try {
            alunoService.excluirAluno(cpf);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    @PutMapping("alunos/alteracao/{cpf}")
    public ResponseEntity<String> alterarAluno(@PathVariable String cpf, @Valid @RequestBody Aluno aluno) {
        try {
            Aluno alunoAlterado = alunoService.alterarAluno(cpf, aluno);
            if (alunoAlterado != null) {
                return ResponseEntity.ok("Dados do aluno alterado com sucesso");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Aluno com CPF " + cpf + " não encontrado");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao alterar aluno");
        }
    }

}
