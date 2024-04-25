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
@Tag(name = "Verificar alunos", description = "Implementando operações especificas")
@RequestMapping("v1")
public class CadastroAlunosControllerV2 implements SwaggerConfigV2 {

    private final VerificaAlunoService verificaAlunoService;

    @Autowired
    public CadastroAlunosControllerV2(VerificaAlunoService verificaAlunoService) {
        this.verificaAlunoService = verificaAlunoService;
    }

    @Override
    @GetMapping("alunos/lista-aprovados")
    public ResponseEntity<List<Aluno>> listarAlunosAprovados(){
        try {
            List<Aluno> alunos = verificaAlunoService.listarAlunosAprovados();
            return ResponseEntity.ok(alunos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Override
    @GetMapping("alunos/lista-reprovados")
    public ResponseEntity<List<Aluno>> listarAlunosReprovadosUmaProva(){
        try {
            List<Aluno> alunos = verificaAlunoService.listarAlunosReprovadosUmaProva();
            return ResponseEntity.ok(alunos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Override
    @GetMapping("alunos/lista-aprovados-turma")
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
