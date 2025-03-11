package services.unitarios;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import br.com.cadastro.alunos.model.entities.Aluno;
import br.com.cadastro.alunos.model.repository.AlunoRepository;
import br.com.cadastro.alunos.model.services.VerificaAlunoService;
@Tag("unitario-verifica-aluno")
@ExtendWith(MockitoExtension.class)
class VerificaAlunoServiceTest {

    @InjectMocks
    private VerificaAlunoService verificaAlunoService;

    @Mock
    private AlunoRepository alunoRepository;

    private Aluno alunoAprovado;
    private Aluno alunoReprovado;
    private Aluno alunoReprovadoUmaProva;

    @BeforeEach
    void setUp() {
        alunoAprovado = new Aluno("123", "Carlos", "Rua A", "Turma 1", 9.0, 8.0, 7.5, "SIM");
        alunoReprovado = new Aluno("124", "Ana", "Rua B", "Turma 2", 5.0, 6.0, 5.5, "NÃO");
        alunoReprovadoUmaProva = new Aluno("125", "João", "Rua C", "Turma 3", 9.0, 0.0, 8.5, "NÃO");
    }

    @Test
    void deveListarAlunosAprovados() {
        when(alunoRepository.findAll()).thenReturn(Arrays.asList(alunoAprovado, alunoReprovado));

        List<Aluno> aprovados = verificaAlunoService.listarAlunosAprovados();

        assertEquals(1, aprovados.size());
        assertEquals("Carlos", aprovados.get(0).getNome());
        verify(alunoRepository, times(1)).findAll();
    }

    @Test
    void deveListarAlunosReprovadosUmaProva() {
        when(alunoRepository.findAll()).thenReturn(Arrays.asList(alunoAprovado, alunoReprovadoUmaProva));

        List<Aluno> reprovadosUmaProva = verificaAlunoService.listarAlunosReprovadosUmaProva();

        assertEquals(1, reprovadosUmaProva.size());
        assertEquals("João", reprovadosUmaProva.get(0).getNome());
        verify(alunoRepository, times(1)).findAll();
    }

    @Test
    void deveListarTodosAlunosReprovados() {
        when(alunoRepository.findAll()).thenReturn(Arrays.asList(alunoAprovado, alunoReprovado));

        List<Aluno> reprovados = verificaAlunoService.listarTodosAlunosReprovados();

        assertEquals(1, reprovados.size());
        assertEquals("Ana", reprovados.get(0).getNome());
        verify(alunoRepository, times(1)).findAll();
    }

    @Test
    void deveBuscarAlunosAprovadosPorTurma() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Aluno> page = new PageImpl<>(List.of(alunoAprovado));
        when(alunoRepository.findByTurmaAndAprovado("Turma 1", "SIM", pageable)).thenReturn(page);

        Page<Aluno> result = verificaAlunoService.buscarAlunosAprovadosPorTurma("Turma 1", 0, 10);

        assertEquals(1, result.getTotalElements());
        assertEquals("Carlos", result.getContent().get(0).getNome());
        verify(alunoRepository, times(1)).findByTurmaAndAprovado("Turma 1", "SIM", pageable);
    }
}