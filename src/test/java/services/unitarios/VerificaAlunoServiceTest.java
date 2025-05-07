package services.unitarios;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import br.com.cadastro.alunos.model.exceptions.ResourceNotFoundException;
import br.com.cadastro.alunos.model.exceptions.ServiceException;
import br.com.cadastro.alunos.model.repository.AlunoRepository;
import br.com.cadastro.alunos.model.services.ConsultaAlunoService;

@Tag("unitario-consulta-aluno")
@ExtendWith(MockitoExtension.class)
class VerificaAlunoServiceTest {

    @InjectMocks
    private ConsultaAlunoService consultaAlunoService;

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

        List<Aluno> aprovados = consultaAlunoService.listarAlunosAprovados();

        assertEquals(1, aprovados.size());
        assertEquals("Carlos", aprovados.get(0).getNome());
        verify(alunoRepository, times(1)).findAll();
    }

    @Test
    void deveListarAlunosAprovadosErro() {
        when(alunoRepository.findAll()).thenThrow(new RuntimeException("Erro ao listar"));

        assertThrows(ServiceException.class, () -> consultaAlunoService.listarAlunosAprovados());
    }

    @Test
    void deveListarAlunosReprovadosUmaProva() {
        when(alunoRepository.findAll()).thenReturn(Arrays.asList(alunoAprovado, alunoReprovadoUmaProva));

        List<Aluno> reprovadosUmaProva = consultaAlunoService.listarAlunosReprovadosUmaProva();

        assertEquals(1, reprovadosUmaProva.size());
        assertEquals("João", reprovadosUmaProva.get(0).getNome());
        verify(alunoRepository, times(1)).findAll();
    }

    @Test
    void deveListarAlunosReprovadosUmaProvaErro() {
        when(alunoRepository.findAll()).thenThrow(new RuntimeException("Erro ao listar"));

        assertThrows(ServiceException.class, () -> consultaAlunoService.listarAlunosReprovadosUmaProva());
    }

    @Test
    void deveListarTodosAlunosReprovados() {
        when(alunoRepository.findAll()).thenReturn(Arrays.asList(alunoAprovado, alunoReprovado));

        List<Aluno> reprovados = consultaAlunoService.listarTodosAlunosReprovados();

        assertEquals(1, reprovados.size());
        assertEquals("Ana", reprovados.get(0).getNome());
        verify(alunoRepository, times(1)).findAll();
    }

    @Test
    void deveListarTodosAlunosReprovadosErro() {
        when(alunoRepository.findAll()).thenThrow(new RuntimeException("Erro ao listar"));

        assertThrows(ServiceException.class, () -> consultaAlunoService.listarTodosAlunosReprovados());
    }

    @Test
    void deveListarTodosAlunos() {
        when(alunoRepository.findAll()).thenReturn(Arrays.asList(alunoAprovado, alunoReprovado));

        List<Aluno> alunos = consultaAlunoService.listarTodosAlunos();

        assertEquals(2, alunos.size());
        verify(alunoRepository, times(1)).findAll();
    }

    @Test
    void deveListarTodosAlunosErro() {
        when(alunoRepository.findAll()).thenThrow(new RuntimeException("Erro ao listar"));

        assertThrows(ServiceException.class, () -> consultaAlunoService.listarTodosAlunos());
    }

    @Test
    void deveBuscarAlunosAprovadosPorTurma() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Aluno> page = new PageImpl<>(List.of(alunoAprovado));
        when(alunoRepository.findByTurmaAndAprovado("Turma 1", "SIM", pageable)).thenReturn(page);

        Page<Aluno> result = consultaAlunoService.buscarAlunosAprovadosPorTurma("Turma 1", 0, 10);

        assertEquals(1, result.getTotalElements());
        assertEquals("Carlos", result.getContent().get(0).getNome());
        verify(alunoRepository, times(1)).findByTurmaAndAprovado("Turma 1", "SIM", pageable);
    }

    @Test
    void deveBuscarAlunosAprovadosPorTurmaVazia() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Aluno> pageVazia = new PageImpl<>(List.of());
        when(alunoRepository.findByTurmaAndAprovado("Turma 1", "SIM", pageable)).thenReturn(pageVazia);

        assertThrows(ResourceNotFoundException.class, () ->
                consultaAlunoService.buscarAlunosAprovadosPorTurma("Turma 1", 0, 10)
        );
    }

    @Test
    void deveBuscarAlunosAprovadosPorTurmaErro() {
        Pageable pageable = PageRequest.of(0, 10);
        when(alunoRepository.findByTurmaAndAprovado("Turma 1", "SIM", pageable))
                .thenThrow(new RuntimeException("Erro ao buscar"));

        assertThrows(ServiceException.class, () ->
                consultaAlunoService.buscarAlunosAprovadosPorTurma("Turma 1", 0, 10)
        );
    }

    @Test
    void deveBuscarAlunosReprovadosPorTurma() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Aluno> page = new PageImpl<>(List.of(alunoReprovado));
        when(alunoRepository.findByTurmaAndAprovado("Turma 2", "NÃO", pageable)).thenReturn(page);

        Page<Aluno> result = consultaAlunoService.buscarAlunosReprovadosPorTurma("Turma 2", 0, 10);

        assertEquals(1, result.getTotalElements());
        assertEquals("Ana", result.getContent().get(0).getNome());
        verify(alunoRepository, times(1)).findByTurmaAndAprovado("Turma 2", "NÃO", pageable);
    }

    @Test
    void deveBuscarTodosAlunosPorTurma() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Aluno> page = new PageImpl<>(Arrays.asList(alunoAprovado, alunoReprovado));
        when(alunoRepository.findByTurma("Turma 1", pageable)).thenReturn(page);

        Page<Aluno> result = consultaAlunoService.buscarTodosAlunosPorTurma("Turma 1", 0, 10);

        assertEquals(2, result.getTotalElements());
        verify(alunoRepository, times(1)).findByTurma("Turma 1", pageable);
    }

    // Testes para as consultas otimizadas

    @Test
    void deveUsarConsultaOtimizadaParaAprovados() {
        when(alunoRepository.findApproved()).thenReturn(List.of(alunoAprovado));

        // Implementar quando as consultas otimizadas estiverem integradas ao serviço
        // List<Aluno> aprovados = consultaAlunoService.listarAlunosAprovadosOtimizado();
        //
        // assertEquals(1, aprovados.size());
        // assertEquals("Carlos", aprovados.get(0).getNome());
        // verify(alunoRepository, times(1)).findApproved();
    }

    @Test
    void deveUsarConsultaOtimizadaParaReprovados() {
        when(alunoRepository.findFailed()).thenReturn(List.of(alunoReprovado));

        // Implementar quando as consultas otimizadas estiverem integradas ao serviço
        // List<Aluno> reprovados = consultaAlunoService.listarAlunosReprovadosOtimizado();
        //
        // assertEquals(1, reprovados.size());
        // assertEquals("Ana", reprovados.get(0).getNome());
        // verify(alunoRepository, times(1)).findFailed();
    }
}