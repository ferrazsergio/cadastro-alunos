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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    private static final Logger logger = LogManager.getLogger(AlunoServiceTest.class);

    @InjectMocks
    private AlunoService alunoService;

    @Mock
    private AlunoRepository alunoRepository;

    @BeforeEach
    void setUp() {
        logger.info("=== Iniciando teste do AlunoService ===");
        logger.debug("Inicializando mocks do Mockito...");
        MockitoAnnotations.openMocks(this);
        logger.debug("Mocks inicializados com sucesso");
    }

    @Test
    void incluirAlunoNovoCadastroSucesso() {
        logger.info("🧪 Testando inclusão de aluno com novo cadastro (sucesso)");

        // Arrange
        Aluno aluno = new Aluno("123.456.789-09", "Carlos", "Rua A", "Turma 1", 9.0, 8.0, 7.0, "SIM");
        logger.debug("Aluno criado para teste: CPF={}, Nome={}, Notas=[{}, {}, {}], Aprovado={}",
                aluno.getCpf(), aluno.getNome(), aluno.getNota1(), aluno.getNota2(), aluno.getNota3(), aluno.getAprovado());

        logger.debug("Configurando mocks...");
        when(alunoRepository.existsById(aluno.getCpf())).thenReturn(false);
        when(alunoRepository.findById(aluno.getCpf())).thenReturn(Optional.empty());
        when(alunoRepository.save(any(Aluno.class))).thenReturn(aluno);
        logger.debug("Mocks configurados: existsById=false, findById=empty, save=retorna aluno");

        // Act
        logger.info("Executando inclusão do aluno...");
        Aluno result = alunoService.incluirAluno(aluno);

        // Assert
        logger.info("Verificando resultado da inclusão...");
        assertNotNull(result);
        assertEquals("Carlos", result.getNome());
        assertEquals("SIM", result.getAprovado()); // Verifica se o status foi preenchido
        verify(alunoRepository, times(1)).save(aluno);

        logger.info("✅ Teste passou! Aluno incluído com sucesso: CPF={}, Nome={}, Status={}",
                result.getCpf(), result.getNome(), result.getAprovado());
    }

    @Test
    void incluirAlunoCpfJaCadastradoFalha() {
        logger.info("🧪 Testando inclusão de aluno com CPF já cadastrado (deve falhar)");

        // Arrange
        Aluno aluno = new Aluno("123.456.789-09", "Carlos", "Rua A", "Turma 1", 9.0, 8.0, 7.0, "SIM");
        logger.debug("Aluno criado para teste: CPF={} (simulando CPF já existente)", aluno.getCpf());

        logger.debug("Configurando mock para CPF já existente...");
        when(alunoRepository.existsById(aluno.getCpf())).thenReturn(true);
        logger.debug("Mock configurado: existsById=true (CPF já existe)");

        // Act & Assert
        logger.info("Executando inclusão que deve falhar...");
        Exception exception = assertThrows(BusinessException.class, () -> alunoService.incluirAluno(aluno));

        logger.info("Verificando exceção lançada...");
        assertEquals("CPF já cadastrado", exception.getMessage());
        verify(alunoRepository, never()).save(aluno);

        logger.info("✅ Teste passou! BusinessException lançada corretamente: {}", exception.getMessage());
    }

    @Test
    void incluirAlunoCpfInvalidoFalha() {
        logger.info("🧪 Testando inclusão de aluno com CPF inválido (deve falhar)");

        // Arrange
        Aluno aluno = new Aluno("123", "Carlos", "Rua A", "Turma 1", 9.0, 8.0, 7.0, "SIM");
        logger.debug("Aluno criado com CPF inválido: CPF='{}' (muito curto)", aluno.getCpf());

        // Act & Assert
        logger.info("Executando inclusão com CPF inválido...");
        Exception exception = assertThrows(BusinessException.class, () -> alunoService.incluirAluno(aluno));

        logger.info("Verificando exceção lançada...");
        assertEquals("O CPF do aluno não é válido", exception.getMessage());
        verify(alunoRepository, never()).save(aluno);

        logger.info("✅ Teste passou! BusinessException lançada corretamente: {}", exception.getMessage());
    }

    @Test
    void alterarAlunoSucesso() {
        logger.info("🧪 Testando alteração de aluno existente (sucesso)");

        // Arrange
        Aluno alunoExistente = new Aluno("123.456.789-09", "Carlos", "Rua A", "Turma 1", 9.0, 8.0, 7.0, "SIM");
        Aluno alunoAlterado = new Aluno("123.456.789-09", "Carlos Atualizado", "Rua B", "Turma 2", 9.5, 9.0, 8.5, null);
        Aluno alunoSalvo = new Aluno("123.456.789-09", "Carlos Atualizado", "Rua B", "Turma 2", 9.5, 9.0, 8.5, "SIM");

        logger.debug("Aluno existente: CPF={}, Nome={}", alunoExistente.getCpf(), alunoExistente.getNome());
        logger.debug("Dados para alteração: Nome={}, Endereço={}, Turma={}",
                alunoAlterado.getNome(), alunoAlterado.getEndereco(), alunoAlterado.getTurma());

        logger.debug("Configurando mocks...");
        when(alunoRepository.findById("123.456.789-09")).thenReturn(Optional.of(alunoExistente));
        when(alunoRepository.save(any(Aluno.class))).thenReturn(alunoSalvo);
        logger.debug("Mocks configurados: findById=aluno existente, save=aluno atualizado");

        // Act
        logger.info("Executando alteração do aluno...");
        Aluno result = alunoService.alterarAluno("123.456.789-09", alunoAlterado);

        // Assert
        logger.info("Verificando resultado da alteração...");
        assertNotNull(result);
        assertEquals("Carlos Atualizado", result.getNome());
        assertEquals("SIM", result.getAprovado()); // Verifica se o status foi recalculado

        logger.info("✅ Teste passou! Aluno alterado com sucesso: CPF={}, Nome atualizado={}, Status={}",
                result.getCpf(), result.getNome(), result.getAprovado());
    }

    @Test
    void alterarAlunoNaoEncontradoFalha() {
        logger.info("🧪 Testando alteração de aluno não encontrado (deve falhar)");

        // Arrange
        Aluno alunoAtualizado = new Aluno("123.456.789-09", "Carlos Atualizado", "Rua B", "Turma 2", 9.5, 9.0, 8.5, "SIM");
        logger.debug("Tentando alterar aluno com CPF={} (não existe)", alunoAtualizado.getCpf());

        logger.debug("Configurando mock para aluno não encontrado...");
        when(alunoRepository.findById("123.456.789-09")).thenReturn(Optional.empty());
        logger.debug("Mock configurado: findById=empty (aluno não encontrado)");

        // Act & Assert
        logger.info("Executando alteração que deve falhar...");
        assertThrows(ResourceNotFoundException.class, () -> alunoService.alterarAluno("123.456.789-09", alunoAtualizado));
        verify(alunoRepository, never()).save(any(Aluno.class));

        logger.info("✅ Teste passou! ResourceNotFoundException lançada corretamente");
    }

    @Test
    void excluirAlunoSucesso() {
        logger.info("🧪 Testando exclusão de aluno existente (sucesso)");

        // Arrange
        String cpf = "123.456.789-09";
        logger.debug("CPF para exclusão: {}", cpf);

        logger.debug("Configurando mocks...");
        when(alunoRepository.existsById(cpf)).thenReturn(true);
        doNothing().when(alunoRepository).deleteById(cpf);
        logger.debug("Mocks configurados: existsById=true, deleteById=void");

        // Act
        logger.info("Executando exclusão do aluno...");
        alunoService.excluirAluno(cpf);

        // Assert
        logger.info("Verificando se exclusão foi executada...");
        verify(alunoRepository, times(1)).deleteById(cpf);

        logger.info("✅ Teste passou! Aluno excluído com sucesso: CPF={}", cpf);
    }

    @Test
    void excluirAlunoNaoEncontradoFalha() {
        logger.info("🧪 Testando exclusão de aluno não encontrado (deve falhar)");

        // Arrange
        String cpf = "123.456.789-09";
        logger.debug("CPF para exclusão: {} (não existe)", cpf);

        logger.debug("Configurando mock para aluno não encontrado...");
        when(alunoRepository.existsById(cpf)).thenReturn(false);
        logger.debug("Mock configurado: existsById=false (aluno não existe)");

        // Act & Assert
        logger.info("Executando exclusão que deve falhar...");
        assertThrows(ResourceNotFoundException.class, () -> alunoService.excluirAluno(cpf));
        verify(alunoRepository, never()).deleteById(anyString());

        logger.info("✅ Teste passou! ResourceNotFoundException lançada corretamente");
    }

    @Test
    void avaliarAlunosSucesso() {
        logger.info("🧪 Testando avaliação de alunos (cálculo automático de aprovação)");

        // Arrange
        Aluno aluno1 = new Aluno("123.456.789-09", "Carlos", "Rua A", "Turma 1", 9.0, 8.0, 7.0, null);
        Aluno aluno2 = new Aluno("124.456.789-09", "Ana", "Rua B", "Turma 1", 6.0, 5.0, 7.0, null);
        List<Aluno> alunos = Arrays.asList(aluno1, aluno2);

        logger.debug("Alunos para avaliação:");
        logger.debug("  - Carlos: Notas=[{}, {}, {}] -> Média={}",
                aluno1.getNota1(), aluno1.getNota2(), aluno1.getNota3(), (aluno1.getNota1() + aluno1.getNota2() + aluno1.getNota3()) / 3);
        logger.debug("  - Ana: Notas=[{}, {}, {}] -> Média={}",
                aluno2.getNota1(), aluno2.getNota2(), aluno2.getNota3(), (aluno2.getNota1() + aluno2.getNota2() + aluno2.getNota3()) / 3);

        logger.debug("Configurando mocks...");
        when(alunoRepository.findAll()).thenReturn(alunos);
        when(alunoRepository.save(any(Aluno.class))).thenAnswer(invocation -> invocation.getArgument(0));
        logger.debug("Mocks configurados: findAll=lista de alunos, save=retorna argumento");

        // Act
        logger.info("Executando avaliação dos alunos...");
        alunoService.avaliarAlunos();

        // Assert
        logger.info("Verificando resultados da avaliação...");
        assertEquals("SIM", aluno1.getAprovado());
        assertEquals("NÃO", aluno2.getAprovado());
        verify(alunoRepository, times(2)).save(any(Aluno.class));

        logger.info("✅ Teste passou! Avaliações realizadas:");
        logger.info("  - Carlos: {} (média > 7.0)", aluno1.getAprovado());
        logger.info("  - Ana: {} (média ≤ 7.0)", aluno2.getAprovado());
    }

    @Test
    void avaliarAlunosErroAoSalvar() {
        logger.info("🧪 Testando avaliação de alunos com erro ao salvar (deve falhar)");

        // Arrange
        List<Aluno> alunos = Arrays.asList(
                new Aluno("123.456.789-09", "Carlos", "Rua A", "Turma 1", 9.0, 8.0, 7.0, null)
        );
        logger.debug("Aluno para avaliação: CPF={}, Nome={}", alunos.get(0).getCpf(), alunos.get(0).getNome());

        logger.debug("Configurando mocks com erro...");
        when(alunoRepository.findAll()).thenReturn(alunos);
        when(alunoRepository.save(any(Aluno.class))).thenThrow(new RuntimeException("Erro ao salvar"));
        logger.debug("Mocks configurados: findAll=lista de alunos, save=lança exceção");

        // Act & Assert
        logger.info("Executando avaliação que deve falhar...");
        assertThrows(ServiceException.class, () -> alunoService.avaliarAlunos());

        logger.info("✅ Teste passou! ServiceException lançada corretamente devido a erro no save");
    }

    @Test
    void listarAlunosSucesso() {
        logger.info("🧪 Testando listagem de alunos (sucesso)");

        // Arrange
        List<Aluno> alunosMock = Arrays.asList(
                new Aluno("123.456.789-09", "Carlos", "Rua A", "Turma 1", 9.0, 8.0, 7.0, "SIM"),
                new Aluno("124.456.789-09", "Ana", "Rua B", "Turma 1", 6.0, 5.0, 7.0, "NÃO")
        );
        logger.debug("Lista mock criada com {} alunos:", alunosMock.size());
        alunosMock.forEach(aluno ->
                logger.debug("  - CPF={}, Nome={}, Status={}", aluno.getCpf(), aluno.getNome(), aluno.getAprovado()));

        logger.debug("Configurando mock...");
        when(alunoRepository.findAll()).thenReturn(alunosMock);
        logger.debug("Mock configurado: findAll=lista com {} alunos", alunosMock.size());

        // Act
        logger.info("Executando listagem de alunos...");
        List<Aluno> result = alunoService.listarAlunos();

        // Assert
        logger.info("Verificando resultado da listagem...");
        assertEquals(2, result.size());
        verify(alunoRepository, times(1)).findAll();

        logger.info("✅ Teste passou! {} alunos listados com sucesso", result.size());
        result.forEach(aluno ->
                logger.debug("  - Listado: CPF={}, Nome={}", aluno.getCpf(), aluno.getNome()));
    }

    @Test
    void listarAlunosErro() {
        logger.info("🧪 Testando listagem de alunos com erro (deve falhar)");

        // Arrange
        logger.debug("Configurando mock com erro...");
        when(alunoRepository.findAll()).thenThrow(new RuntimeException("Erro ao listar"));
        logger.debug("Mock configurado: findAll=lança exceção");

        // Act & Assert
        logger.info("Executando listagem que deve falhar...");
        assertThrows(ServiceException.class, () -> alunoService.listarAlunos());

        logger.info("✅ Teste passou! ServiceException lançada corretamente devido a erro no findAll");
    }
}