package services.unitarios;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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
        Aluno aluno = new Aluno("123", "Carlos", "Rua A", "Turma 1", 9.0, 8.0, 7.0, "SIM");

        when(alunoRepository.existsById(aluno.getCpf())).thenReturn(false);
        when(alunoRepository.isCpfRegisteredInDifferentTurma(any(), any())).thenReturn(false);
        when(alunoRepository.save(any(Aluno.class))).thenReturn(aluno);

        Aluno result = alunoService.incluirAluno(aluno);
        assertNotNull(result);
        assertEquals("Carlos", result.getNome());
        verify(alunoRepository, times(1)).save(aluno);
    }

    @Test
    void incluirAlunoCpfJaCadastradoFalha() {
        Aluno aluno = new Aluno("123", "Carlos", "Rua A", "Turma 1", 9.0, 8.0, 7.0, "SIM");
        when(alunoRepository.existsById(aluno.getCpf())).thenReturn(true);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> alunoService.incluirAluno(aluno));
        assertEquals("CPF já cadastrado", exception.getMessage());
        verify(alunoRepository, never()).save(aluno);
    }

    @Test
    void alterarAlunoSucesso() {
        Aluno alunoExistente = new Aluno("123", "Carlos", "Rua A", "Turma 1", 9.0, 8.0, 7.0, "SIM");
        Aluno alunoAtualizado = new Aluno("123", "Carlos Atualizado", "Rua B", "Turma 2", 9.5, 9.0, 8.5, "SIM");

        when(alunoRepository.findByCpf("123")).thenReturn(alunoExistente);
        when(alunoRepository.save(any(Aluno.class))).thenReturn(alunoAtualizado);

        Aluno result = alunoService.alterarAluno("123", alunoAtualizado);
        assertNotNull(result);
        assertEquals("Carlos Atualizado", result.getNome());
    }

    @Test
    void excluirAlunoSucesso() {
        Aluno alunoExistente = new Aluno("123", "Carlos", "Rua A", "Turma 1", 9.0, 8.0, 7.0, "SIM");

        when(alunoRepository.existsById("123")).thenReturn(true);
        when(alunoRepository.findById("123")).thenReturn(Optional.of(alunoExistente));
        doNothing().when(alunoRepository).deleteById("123");

        alunoService.excluirAluno("123");

        verify(alunoRepository, times(1)).deleteById("123");
    }


    @Test
    void avaliarAlunosSucesso() {
        Aluno aluno1 = new Aluno("123", "Carlos", "Rua A", "Turma 1", 9.0, 8.0, 7.0, "SIM");
        Aluno aluno2 = new Aluno("124", "Ana", "Rua B", "Turma 1", 6.0, 5.0, 7.0, "NÃO");
        List<Aluno> alunos = Arrays.asList(aluno1, aluno2);

        when(alunoRepository.findAll()).thenReturn(alunos);
        when(alunoRepository.save(any(Aluno.class))).thenAnswer(invocation -> invocation.getArgument(0));

        alunoService.avaliarAlunos();

        assertEquals("SIM", aluno1.getAprovado());
        assertEquals("NÃO", aluno2.getAprovado());
        verify(alunoRepository, times(2)).save(any(Aluno.class));
    }
    
    @Test
    void listarAlunosSucesso() {
        List<Aluno> alunosMock = Arrays.asList(
                new Aluno("123", "Carlos", "Rua A", "Turma 1", 9.0, 8.0, 7.0, "SIM"),
                new Aluno("124", "Ana", "Rua B", "Turma 1", 6.0, 5.0, 7.0, "NÃO")
        );
        when(alunoRepository.findAll()).thenReturn(alunosMock);

        List<Aluno> result = alunoService.listarAlunos();
        assertEquals(2, result.size());
        verify(alunoRepository, times(1)).findAll();
    }

}
