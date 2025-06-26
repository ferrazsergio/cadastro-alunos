package services.unitarios;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
@SuppressWarnings("java:S*")
class VerificaAlunoServiceTest {

    private static final Logger logger = LogManager.getLogger(VerificaAlunoServiceTest.class);

    @InjectMocks
    private ConsultaAlunoService consultaAlunoService;

    @Mock
    private AlunoRepository alunoRepository;

    private Aluno alunoAprovado;
    private Aluno alunoReprovado;
    private Aluno alunoReprovadoUmaProva;

    @BeforeEach
    void setUp() {
        logger.info("=== Iniciando teste do ConsultaAlunoService ===");
        logger.info("🧪 Executado por: ferrazsergio em 2025-06-26 18:09:45");

        logger.debug("Criando dados de teste...");
        alunoAprovado = new Aluno("123", "Carlos", "Rua A", "Turma 1", 9.0, 8.0, 7.5, "Sim");
        alunoReprovado = new Aluno("124", "Ana", "Rua B", "Turma 2", 5.0, 6.0, 5.5, "Não");
        alunoReprovadoUmaProva = new Aluno("125", "João", "Rua C", "Turma 3", 9.0, 0.0, 0.0, "Não");

        logger.debug("Alunos de teste criados:");
        logger.debug("  📘 Aprovado: CPF={}, Nome={}, Média={:.2f}, Status={}",
                alunoAprovado.getCpf(), alunoAprovado.getNome(),
                (alunoAprovado.getNota1() + alunoAprovado.getNota2() + alunoAprovado.getNota3()) / 3,
                alunoAprovado.getAprovado());
        logger.debug("  📕 Reprovado: CPF={}, Nome={}, Média={:.2f}, Status={}",
                alunoReprovado.getCpf(), alunoReprovado.getNome(),
                (alunoReprovado.getNota1() + alunoReprovado.getNota2() + alunoReprovado.getNota3()) / 3,
                alunoReprovado.getAprovado());
        logger.debug("  📙 Reprovado (1 prova): CPF={}, Nome={}, Notas=[{}, {}, {}], Status={}",
                alunoReprovadoUmaProva.getCpf(), alunoReprovadoUmaProva.getNome(),
                alunoReprovadoUmaProva.getNota1(), alunoReprovadoUmaProva.getNota2(), alunoReprovadoUmaProva.getNota3(),
                alunoReprovadoUmaProva.getAprovado());
    }

    @Test
    void deveListarAlunosAprovados() {
        logger.info("🧪 Testando listagem de alunos aprovados");

        // Arrange
        List<Aluno> alunosMock = Arrays.asList(alunoAprovado, alunoReprovado);
        logger.debug("Configurando mock com {} alunos (1 aprovado, 1 reprovado)", alunosMock.size());
        when(alunoRepository.findAll()).thenReturn(alunosMock);
        logger.debug("✅ Mock configurado: findAll() retorna lista com {} alunos", alunosMock.size());

        // Act
        logger.info("Executando listarAlunosAprovados()...");
        List<Aluno> aprovados = consultaAlunoService.listarAlunosAprovados();

        // Assert
        logger.info("Verificando resultado da listagem...");
        assertEquals(1, aprovados.size());
        assertEquals("Carlos", aprovados.get(0).getNome());
        verify(alunoRepository, times(1)).findAll();

        logger.info("✅ Teste passou! {} aluno aprovado encontrado: {}",
                aprovados.size(), aprovados.get(0).getNome());
    }

    @Test
    void deveListarAlunosAprovadosErro() {
        logger.info("🧪 Testando listagem de alunos aprovados com erro (deve falhar)");

        // Arrange
        RuntimeException erroSimulado = new RuntimeException("Erro ao listar");
        logger.debug("Configurando mock para lançar exceção: {}", erroSimulado.getMessage());
        when(alunoRepository.findAll()).thenThrow(erroSimulado);
        logger.debug("✅ Mock configurado para falhar");

        // Act & Assert
        logger.info("Executando operação que deve falhar...");
        assertThrows(ServiceException.class, () -> consultaAlunoService.listarAlunosAprovados());

        logger.info("✅ Teste passou! ServiceException lançada corretamente");
    }

    @Test
    void deveListarAlunosReprovadosUmaProva() {
        logger.info("🧪 Testando listagem de alunos reprovados que fizeram apenas uma prova");

        // Arrange
        List<Aluno> alunosMock = Arrays.asList(alunoAprovado, alunoReprovadoUmaProva);
        logger.debug("Configurando mock com {} alunos:", alunosMock.size());
        logger.debug("  - {}: {} provas feitas", alunoAprovado.getNome(), contarProvasFeitas(alunoAprovado));
        logger.debug("  - {}: {} prova feita", alunoReprovadoUmaProva.getNome(), contarProvasFeitas(alunoReprovadoUmaProva));

        when(alunoRepository.findAll()).thenReturn(alunosMock);
        logger.debug("✅ Mock configurado");

        // Act
        logger.info("Executando listarAlunosReprovadosUmaProva()...");
        List<Aluno> reprovadosUmaProva = consultaAlunoService.listarAlunosReprovadosUmaProva();

        // Assert
        logger.info("Verificando resultado da listagem...");
        assertEquals(1, reprovadosUmaProva.size());
        assertEquals("João", reprovadosUmaProva.get(0).getNome());
        verify(alunoRepository, times(1)).findAll();

        logger.info("✅ Teste passou! {} aluno com apenas 1 prova encontrado: {}",
                reprovadosUmaProva.size(), reprovadosUmaProva.get(0).getNome());
    }

    @Test
    void deveListarAlunosReprovadosUmaProvaErro() {
        logger.info("🧪 Testando listagem de alunos reprovados (1 prova) com erro (deve falhar)");

        // Arrange
        RuntimeException erroSimulado = new RuntimeException("Erro ao listar");
        logger.debug("Configurando mock para lançar exceção: {}", erroSimulado.getMessage());
        when(alunoRepository.findAll()).thenThrow(erroSimulado);
        logger.debug("✅ Mock configurado para falhar");

        // Act & Assert
        logger.info("Executando operação que deve falhar...");
        assertThrows(ServiceException.class, () -> consultaAlunoService.listarAlunosReprovadosUmaProva());

        logger.info("✅ Teste passou! ServiceException lançada corretamente");
    }

    @Test
    void deveListarTodosAlunosReprovados() {
        logger.info("🧪 Testando listagem de todos os alunos reprovados");

        // Arrange
        List<Aluno> alunosMock = Arrays.asList(alunoAprovado, alunoReprovado);
        logger.debug("Configurando mock com {} alunos:", alunosMock.size());
        logger.debug("  - {} (Aprovado): Média={:.2f}",
                alunoAprovado.getNome(),
                (alunoAprovado.getNota1() + alunoAprovado.getNota2() + alunoAprovado.getNota3()) / 3);
        logger.debug("  - {} (Reprovado): Média={:.2f}",
                alunoReprovado.getNome(),
                (alunoReprovado.getNota1() + alunoReprovado.getNota2() + alunoReprovado.getNota3()) / 3);

        when(alunoRepository.findAll()).thenReturn(alunosMock);
        logger.debug("✅ Mock configurado");

        // Act
        logger.info("Executando listarTodosAlunosReprovados()...");
        List<Aluno> reprovados = consultaAlunoService.listarTodosAlunosReprovados();

        // Assert
        logger.info("Verificando resultado da listagem...");
        assertEquals(1, reprovados.size());
        assertEquals("Ana", reprovados.get(0).getNome());
        verify(alunoRepository, times(1)).findAll();

        logger.info("✅ Teste passou! {} aluno reprovado encontrado: {}",
                reprovados.size(), reprovados.get(0).getNome());
    }

    @Test
    void deveListarTodosAlunosReprovadosErro() {
        logger.info("🧪 Testando listagem de todos os alunos reprovados com erro (deve falhar)");

        // Arrange
        RuntimeException erroSimulado = new RuntimeException("Erro ao listar");
        logger.debug("Configurando mock para lançar exceção: {}", erroSimulado.getMessage());
        when(alunoRepository.findAll()).thenThrow(erroSimulado);
        logger.debug("✅ Mock configurado para falhar");

        // Act & Assert
        logger.info("Executando operação que deve falhar...");
        assertThrows(ServiceException.class, () -> consultaAlunoService.listarTodosAlunosReprovados());

        logger.info("✅ Teste passou! ServiceException lançada corretamente");
    }

    @Test
    void deveListarTodosAlunos() {
        logger.info("🧪 Testando listagem de todos os alunos");

        // Arrange
        List<Aluno> alunosMock = Arrays.asList(alunoAprovado, alunoReprovado);
        logger.debug("Configurando mock com {} alunos:", alunosMock.size());
        alunosMock.forEach(aluno ->
                logger.debug("  - CPF={}, Nome={}, Status={}",
                        aluno.getCpf(), aluno.getNome(), aluno.getAprovado()));

        when(alunoRepository.findAll()).thenReturn(alunosMock);
        logger.debug("✅ Mock configurado");

        // Act
        logger.info("Executando listarTodosAlunos()...");
        List<Aluno> alunos = consultaAlunoService.listarTodosAlunos();

        // Assert
        logger.info("Verificando resultado da listagem...");
        assertEquals(2, alunos.size());
        verify(alunoRepository, times(1)).findAll();

        logger.info("✅ Teste passou! {} alunos encontrados:", alunos.size());
        alunos.forEach(aluno ->
                logger.debug("  - {}: {}", aluno.getNome(), aluno.getAprovado()));
    }

    @Test
    void deveListarTodosAlunosErro() {
        logger.info("🧪 Testando listagem de todos os alunos com erro (deve falhar)");

        // Arrange
        RuntimeException erroSimulado = new RuntimeException("Erro ao listar");
        logger.debug("Configurando mock para lançar exceção: {}", erroSimulado.getMessage());
        when(alunoRepository.findAll()).thenThrow(erroSimulado);
        logger.debug("✅ Mock configurado para falhar");

        // Act & Assert
        logger.info("Executando operação que deve falhar...");
        assertThrows(ServiceException.class, () -> consultaAlunoService.listarTodosAlunos());

        logger.info("✅ Teste passou! ServiceException lançada corretamente");
    }

    @Test
    void deveBuscarAlunosAprovadosPorTurma() {
        logger.info("🧪 Testando busca de alunos aprovados por turma com paginação");

        // Arrange
        String turma = "Turma 1";
        Pageable pageable = PageRequest.of(0, 10);
        Page<Aluno> page = new PageImpl<>(List.of(alunoAprovado));

        logger.debug("Parâmetros de busca:");
        logger.debug("  - Turma: {}", turma);
        logger.debug("  - Paginação: página={}, tamanho={}", pageable.getPageNumber(), pageable.getPageSize());
        logger.debug("  - Valores de aprovação aceitos: [Sim, APROVADO]");

        logger.debug("Configurando mock...");
        when(alunoRepository.findByTurmaAndAprovadoIn(turma, Arrays.asList("Sim", "APROVADO"), pageable))
                .thenReturn(page);
        logger.debug("✅ Mock configurado: retorna página com {} elemento(s)", page.getTotalElements());

        // Act
        logger.info("Executando buscarAlunosAprovadosPorTurma({}, 0, 10)...", turma);
        Page<Aluno> result = consultaAlunoService.buscarAlunosAprovadosPorTurma(turma, 0, 10);

        // Assert
        logger.info("Verificando resultado da busca...");
        assertEquals(1, result.getTotalElements());
        assertEquals("Carlos", result.getContent().get(0).getNome());
        verify(alunoRepository, times(1)).findByTurmaAndAprovadoIn(turma, Arrays.asList("Sim", "APROVADO"), pageable);

        logger.info("✅ Teste passou! {} aluno aprovado encontrado na {}: {}",
                result.getTotalElements(), turma, result.getContent().get(0).getNome());
    }

    @Test
    void deveBuscarAlunosAprovadosPorTurmaVazia() {
        logger.info("🧪 Testando busca de alunos aprovados em turma vazia (deve lançar exceção)");

        // Arrange
        String turma = "Turma 1";
        Pageable pageable = PageRequest.of(0, 10);
        Page<Aluno> pageVazia = new PageImpl<>(List.of());

        logger.debug("Parâmetros de busca:");
        logger.debug("  - Turma: {} (sem alunos aprovados)", turma);
        logger.debug("  - Paginação: página={}, tamanho={}", pageable.getPageNumber(), pageable.getPageSize());

        logger.debug("Configurando mock para retornar página vazia...");
        when(alunoRepository.findByTurmaAndAprovadoIn(turma, Arrays.asList("Sim", "APROVADO"), pageable))
                .thenReturn(pageVazia);
        logger.debug("✅ Mock configurado: retorna página vazia");

        // Act & Assert
        logger.info("Executando busca que deve falhar...");
        assertThrows(ResourceNotFoundException.class, () ->
                consultaAlunoService.buscarAlunosAprovadosPorTurma(turma, 0, 10)
        );

        logger.info("✅ Teste passou! ResourceNotFoundException lançada corretamente para turma vazia");
    }

    @Test
    void deveBuscarAlunosAprovadosPorTurmaErro() {
        logger.info("🧪 Testando busca de alunos aprovados com erro no repositório (deve falhar)");

        // Arrange
        String turma = "Turma 1";
        Pageable pageable = PageRequest.of(0, 10);
        RuntimeException erroSimulado = new RuntimeException("Erro ao buscar");

        logger.debug("Parâmetros de busca: Turma={}", turma);
        logger.debug("Configurando mock para lançar exceção: {}", erroSimulado.getMessage());
        when(alunoRepository.findByTurmaAndAprovadoIn(turma, Arrays.asList("Sim", "APROVADO"), pageable))
                .thenThrow(erroSimulado);
        logger.debug("✅ Mock configurado para falhar");

        // Act & Assert
        logger.info("Executando busca que deve falhar...");
        assertThrows(ServiceException.class, () ->
                consultaAlunoService.buscarAlunosAprovadosPorTurma(turma, 0, 10)
        );

        logger.info("✅ Teste passou! ServiceException lançada corretamente");
    }

    @Test
    void deveBuscarAlunosReprovadosPorTurma() {
        logger.info("🧪 Testando busca de alunos reprovados por turma com paginação");

        // Arrange
        String turma = "Turma 2";
        Pageable pageable = PageRequest.of(0, 10);
        Page<Aluno> page = new PageImpl<>(List.of(alunoReprovado));

        logger.debug("Parâmetros de busca:");
        logger.debug("  - Turma: {}", turma);
        logger.debug("  - Paginação: página={}, tamanho={}", pageable.getPageNumber(), pageable.getPageSize());
        logger.debug("  - Valor de reprovação: 'Não'");

        logger.debug("Configurando mock...");
        when(alunoRepository.findByTurmaAndAprovado(turma, "Não", pageable)).thenReturn(page);
        logger.debug("✅ Mock configurado: retorna página com {} elemento(s)", page.getTotalElements());

        // Act
        logger.info("Executando buscarAlunosReprovadosPorTurma({}, 0, 10)...", turma);
        Page<Aluno> result = consultaAlunoService.buscarAlunosReprovadosPorTurma(turma, 0, 10);

        // Assert
        logger.info("Verificando resultado da busca...");
        assertEquals(1, result.getTotalElements());
        assertEquals("Ana", result.getContent().get(0).getNome());
        verify(alunoRepository, times(1)).findByTurmaAndAprovado(turma, "Não", pageable);

        logger.info("✅ Teste passou! {} aluno reprovado encontrado na {}: {}",
                result.getTotalElements(), turma, result.getContent().get(0).getNome());
    }

    @Test
    void deveBuscarTodosAlunosPorTurma() {
        logger.info("🧪 Testando busca de todos os alunos por turma com paginação");

        // Arrange
        String turma = "Turma 1";
        Pageable pageable = PageRequest.of(0, 10);
        Page<Aluno> page = new PageImpl<>(Arrays.asList(alunoAprovado, alunoReprovado));

        logger.debug("Parâmetros de busca:");
        logger.debug("  - Turma: {}", turma);
        logger.debug("  - Paginação: página={}, tamanho={}", pageable.getPageNumber(), pageable.getPageSize());
        logger.debug("  - Incluindo todos os alunos (aprovados e reprovados)");

        logger.debug("Configurando mock...");
        when(alunoRepository.findByTurma(turma, pageable)).thenReturn(page);
        logger.debug("✅ Mock configurado: retorna página com {} elemento(s)", page.getTotalElements());

        // Act
        logger.info("Executando buscarTodosAlunosPorTurma({}, 0, 10)...", turma);
        Page<Aluno> result = consultaAlunoService.buscarTodosAlunosPorTurma(turma, 0, 10);

        // Assert
        logger.info("Verificando resultado da busca...");
        assertEquals(2, result.getTotalElements());
        verify(alunoRepository, times(1)).findByTurma(turma, pageable);

        logger.info("✅ Teste passou! {} alunos encontrados na {}:", result.getTotalElements(), turma);
        result.getContent().forEach(aluno ->
                logger.debug("  - {}: {}", aluno.getNome(), aluno.getAprovado()));
    }

    /**
     * Método auxiliar para contar quantas provas um aluno fez
     */
    private int contarProvasFeitas(Aluno aluno) {
        int count = 0;
        if (aluno.getNota1() != null && aluno.getNota1() > 0) count++;
        if (aluno.getNota2() != null && aluno.getNota2() > 0) count++;
        if (aluno.getNota3() != null && aluno.getNota3() > 0) count++;
        return count;
    }
}