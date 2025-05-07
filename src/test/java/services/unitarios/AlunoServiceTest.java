package services.unitarios;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.cadastro.alunos.model.entities.Aluno;
import br.com.cadastro.alunos.model.exceptions.BusinessException;
import br.com.cadastro.alunos.model.exceptions.ResourceNotFoundException;
import br.com.cadastro.alunos.model.exceptions.ServiceException;
import br.com.cadastro.alunos.model.repository.AlunoRepository;
import br.com.cadastro.alunos.model.services.AlunoService;

@Tag("unitario")
class AlunoServiceTest {

    @InjectMocks
    private AlunoService alunoService;

    @Mock
    private AlunoRepository alunoRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void incluirAlunoNovoCadastroSucesso() {
        Aluno aluno = new Aluno("123.456.789-09", "Carlos", "Rua A", "Turma 1", 9.0, 8.0, 7.0, "SIM");

        when(alunoRepository.existsById(aluno.getCpf())).thenReturn(false);
        when(alunoRepository.findById(aluno.getCpf())).thenReturn(Optional.empty());
        when(alunoRepository.save(any(Aluno.class))).thenReturn(aluno);

        Aluno result = alunoService.incluirAluno(aluno);
        assertNotNull(result);
        assertEquals("Carlos", result.getNome());
        assertEquals("SIM", result.getAprovado()); // Verifica se o status foi preenchido
        verify(alunoRepository, times(1)).save(aluno);
    }

    @Test
    void incluirAlunoCpfJaCadastradoFalha() {
        Aluno aluno = new Aluno("123.456.789-09", "Carlos", "Rua A", "Turma 1", 9.0, 8.0, 7.0, "SIM");
        when(alunoRepository.existsById(aluno.getCpf())).thenReturn(true);

        Exception exception = assertThrows(BusinessException.class, () -> alunoService.incluirAluno(aluno));
        assertEquals("CPF já cadastrado", exception.getMessage());
        verify(alunoRepository, never()).save(aluno);
    }

    @Test
    void incluirAlunoCpfInvalidoFalha() {
        Aluno aluno = new Aluno("123", "Carlos", "Rua A", "Turma 1", 9.0, 8.0, 7.0, "SIM");

        Exception exception = assertThrows(BusinessException.class, () -> alunoService.incluirAluno(aluno));
        assertEquals("O CPF do aluno não é válido", exception.getMessage());
        verify(alunoRepository, never()).save(aluno);
    }

    @Test
    void alterarAlunoSucesso() {
        Aluno alunoExistente = new Aluno("123.456.789-09", "Carlos", "Rua A", "Turma 1", 9.0, 8.0, 7.0, "SIM");
        Aluno alunoAlterado = new Aluno("123.456.789-09", "Carlos Atualizado", "Rua B", "Turma 2", 9.5, 9.0, 8.5, null);
        Aluno alunoSalvo = new Aluno("123.456.789-09", "Carlos Atualizado", "Rua B", "Turma 2", 9.5, 9.0, 8.5, "SIM");

        when(alunoRepository.findById("123.456.789-09")).thenReturn(Optional.of(alunoExistente));
        when(alunoRepository.save(any(Aluno.class))).thenReturn(alunoSalvo);

        Aluno result = alunoService.alterarAluno("123.456.789-09", alunoAlterado);
        assertNotNull(result);
        assertEquals("Carlos Atualizado", result.getNome());
        assertEquals("SIM", result.getAprovado()); // Verifica se o status foi recalculado
    }

    @Test
    void alterarAlunoNaoEncontradoFalha() {
        Aluno alunoAtualizado = new Aluno("123.456.789-09", "Carlos Atualizado", "Rua B", "Turma 2", 9.5, 9.0, 8.5, "SIM");

        when(alunoRepository.findById("123.456.789-09")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> alunoService.alterarAluno("123.456.789-09", alunoAtualizado));
        verify(alunoRepository, never()).save(any(Aluno.class));
    }

    @Test
    void excluirAlunoSucesso() {
        when(alunoRepository.existsById("123.456.789-09")).thenReturn(true);
        doNothing().when(alunoRepository).deleteById("123.456.789-09");

        alunoService.excluirAluno("123.456.789-09");

        verify(alunoRepository, times(1)).deleteById("123.456.789-09");
    }

    @Test
    void excluirAlunoNaoEncontradoFalha() {
        when(alunoRepository.existsById("123.456.789-09")).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> alunoService.excluirAluno("123.456.789-09"));
        verify(alunoRepository, never()).deleteById(anyString());
    }

    @Test
    void avaliarAlunosSucesso() {
        Aluno aluno1 = new Aluno("123.456.789-09", "Carlos", "Rua A", "Turma 1", 9.0, 8.0, 7.0, null);
        Aluno aluno2 = new Aluno("124.456.789-09", "Ana", "Rua B", "Turma 1", 6.0, 5.0, 7.0, null);
        List<Aluno> alunos = Arrays.asList(aluno1, aluno2);

        when(alunoRepository.findAll()).thenReturn(alunos);
        when(alunoRepository.save(any(Aluno.class))).thenAnswer(invocation -> invocation.getArgument(0));

        alunoService.avaliarAlunos();

        assertEquals("SIM", aluno1.getAprovado());
        assertEquals("NÃO", aluno2.getAprovado());
        verify(alunoRepository, times(2)).save(any(Aluno.class));
    }

    @Test
    void avaliarAlunosErroAoSalvar() {
        List<Aluno> alunos = Arrays.asList(
                new Aluno("123.456.789-09", "Carlos", "Rua A", "Turma 1", 9.0, 8.0, 7.0, null)
        );

        when(alunoRepository.findAll()).thenReturn(alunos);
        when(alunoRepository.save(any(Aluno.class))).thenThrow(new RuntimeException("Erro ao salvar"));

        assertThrows(ServiceException.class, () -> alunoService.avaliarAlunos());
    }

    @Test
    void listarAlunosSucesso() {
        List<Aluno> alunosMock = Arrays.asList(
                new Aluno("123.456.789-09", "Carlos", "Rua A", "Turma 1", 9.0, 8.0, 7.0, "SIM"),
                new Aluno("124.456.789-09", "Ana", "Rua B", "Turma 1", 6.0, 5.0, 7.0, "NÃO")
        );
        when(alunoRepository.findAll()).thenReturn(alunosMock);

        List<Aluno> result = alunoService.listarAlunos();
        assertEquals(2, result.size());
        verify(alunoRepository, times(1)).findAll();
    }

    @Test
    void listarAlunosErro() {
        when(alunoRepository.findAll()).thenThrow(new RuntimeException("Erro ao listar"));

        assertThrows(ServiceException.class, () -> alunoService.listarAlunos());
    }
}