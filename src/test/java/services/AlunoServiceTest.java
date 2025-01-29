package services;

import br.com.cadastro.alunos.model.entities.Aluno;
import br.com.cadastro.alunos.model.repository.AlunoRepository;
import br.com.cadastro.alunos.model.services.AlunoService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
    void listarAlunos_deveRetornarListaDeAlunos() {
        // Arrange
        List<Aluno> alunosMock = new ArrayList<>();
        alunosMock.add(new Aluno("123", "João Silva", "Endereço 1", "Turma A", 9.0, 8.0, 7.5, null));
        when(alunoRepository.findAll()).thenReturn(alunosMock);

        // Act
        List<Aluno> resultado = alunoService.listarAlunos();

        // Assert
        assertEquals(1, resultado.size());
        assertEquals("João Silva", resultado.get(0).getNome());
    }
    
    @Test
    void incluirAluno_deveLancarExcecaoQuandoCpfDuplicado() {
        // Arrange
        Aluno aluno = new Aluno("123", "Maria", "Endereço 2", "Turma B", 8.5, 7.0, 9.0, null);
        when(alunoRepository.existsById(aluno.getCpf())).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> alunoService.incluirAluno(aluno));
        assertEquals("CPF já cadastrado", exception.getMessage());
    }

    @Test
    void incluirAluno_deveSalvarAlunoQuandoDadosValidos() {
        // Arrange
        Aluno aluno = new Aluno("123", "Carlos", "Endereço 3", "Turma C", 9.0, 8.5, 7.0, null);
        when(alunoRepository.existsById(aluno.getCpf())).thenReturn(false);
        when(alunoRepository.isCpfRegisteredInDifferentTurma(aluno.getCpf(), aluno.getTurma())).thenReturn(false);
        when(alunoRepository.save(aluno)).thenReturn(aluno);

        // Act
        Aluno resultado = alunoService.incluirAluno(aluno);

        // Assert
        assertNotNull(resultado);
        assertEquals("Carlos", resultado.getNome());
    }

    @Test
    void alterarAluno_deveLancarExcecaoQuandoAlunoNaoEncontrado() {
        // Arrange
        String cpf = "123";
        Aluno aluno = new Aluno(cpf, "Carlos Silva", "Endereço 3", "Turma C", 9.0, 8.5, 7.0, null);
        when(alunoRepository.findByCpf(cpf)).thenReturn(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> alunoService.alterarAluno(cpf, aluno));
        assertEquals("Aluno com CPF 123 não encontrado", exception.getMessage());
    }

    @Test
    void excluirAluno_deveChamarMetodoDeleteDoRepository() {
        // Arrange
        String cpf = "123";

        // Act
        alunoService.excluirAluno(cpf);

        // Assert
        verify(alunoRepository, times(1)).deleteById(cpf);
    }

}
