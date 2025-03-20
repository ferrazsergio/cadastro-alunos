package br.com.cadastro.alunos.api.controller.v2;

import br.com.cadastro.alunos.config.SwaggerConfigV2;
import br.com.cadastro.alunos.model.entities.Aluno;
import br.com.cadastro.alunos.model.services.VerificaAlunoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v2/alunos")
@Tag(name = "Verificar alunos", description = "Implementando operações específicas")
public class CadastroAlunosControllerV2 implements SwaggerConfigV2 {

    private final VerificaAlunoService verificaAlunoService;

    @Autowired
    public CadastroAlunosControllerV2(VerificaAlunoService verificaAlunoService) {
        this.verificaAlunoService = verificaAlunoService;
    }
    @Override
    @GetMapping("/aprovados")
    public ResponseEntity<List<Aluno>> listarAlunosAprovados() {
        try {
            List<Aluno> alunos = verificaAlunoService.listarAlunosAprovados();
            return ResponseEntity.ok(alunos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    @Override
    @GetMapping("/reprovados")
    public ResponseEntity<List<Aluno>> listarAlunosReprovados(@RequestParam(value = "tipo", required = false) String tipo) {
        try {
            List<Aluno> alunos;
            if ("uma-prova".equals(tipo)) {
                alunos = verificaAlunoService.listarAlunosReprovadosUmaProva();
            } else {
                alunos = verificaAlunoService.listarTodosAlunosReprovados();
            }

            if (alunos.isEmpty()) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.ok(alunos);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    @Override
    @GetMapping("/aprovados/por-turma")
    public ResponseEntity<Page<Aluno>> buscarAlunosAprovadosPorTurma(
            @RequestParam String turma,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize) {
        try {
            Page<Aluno> alunos = verificaAlunoService.buscarAlunosAprovadosPorTurma(turma, pageNumber, pageSize);
            return ResponseEntity.ok(alunos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}