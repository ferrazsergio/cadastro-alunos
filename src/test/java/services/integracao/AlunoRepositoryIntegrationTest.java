package services.integracao;

import br.com.cadastro.alunos.model.entities.Aluno;
import br.com.cadastro.alunos.model.repository.AlunoRepository;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.TransactionSystemException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Tag("integracao")
@SpringBootTest(classes = br.com.cadastro.alunos.CadastroAlunosApplication.class)
@ActiveProfiles("test")  // Para carregar o application-test.properties
@SuppressWarnings("java:S*")
class AlunoRepositoryIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(AlunoRepositoryIntegrationTest.class);

    @Autowired
    private AlunoRepository alunoRepository;

    @BeforeEach
    void setUp() {
        logger.info("=== Iniciando teste de integração do AlunoRepository ===");
        logger.info("🧪 Executado por: ferrazsergio em 2025-06-26 18:17:07");
        logger.info("📊 Profile ativo: test (usando H2 database)");
        logger.info("🗄️ Spring Boot Context carregado com sucesso");
    }

    @Test
    void salvarAlunoBancoH2() {
        logger.info("🧪 Testando salvamento de aluno válido no banco H2");

        // Arrange
        Aluno aluno = Aluno.builder()
                .cpf("123.456.789-01") // CPF com 14 caracteres (formato correto)
                .nome("João da Silva Souza")
                .endereco("Rua Teste, 123, Bairro Exemplo, Cidade, Estado, CEP 12345-678")
                .turma("1001B")
                .nota1(9.0)
                .nota2(8.5)
                .nota3(9.2)
                .aprovado("SIM")
                .build();

        logger.debug("📝 Dados do aluno criado para inserção:");
        logger.debug("  - CPF: {} (tamanho: {})", aluno.getCpf(), aluno.getCpf().length());
        logger.debug("  - Nome: {}", aluno.getNome());
        logger.debug("  - Endereço: {} (tamanho: {})", aluno.getEndereco(), aluno.getEndereco().length());
        logger.debug("  - Turma: {}", aluno.getTurma());
        logger.debug("  - Notas: [{}, {}, {}] -> Média: {:.2f}",
                aluno.getNota1(), aluno.getNota2(), aluno.getNota3(),
                (aluno.getNota1() + aluno.getNota2() + aluno.getNota3()) / 3);
        logger.debug("  - Status: {}", aluno.getAprovado());

        // Act
        logger.info("💾 Salvando aluno no banco de dados H2...");
        long startTime = System.currentTimeMillis();
        Aluno alunoSalvo = alunoRepository.save(aluno);
        long endTime = System.currentTimeMillis();

        logger.debug("⏱️ Tempo de salvamento: {}ms", (endTime - startTime));

        // Assert
        logger.info("✅ Verificando dados salvos no banco...");
        assertThat(alunoSalvo).isNotNull();
        assertThat(alunoSalvo.getCpf()).isEqualTo(aluno.getCpf());

        logger.debug("📋 Dados confirmados no banco:");
        logger.debug("  - CPF salvo: {}", alunoSalvo.getCpf());
        logger.debug("  - Nome salvo: {}", alunoSalvo.getNome());
        logger.debug("  - ID gerado: {}", alunoSalvo.getCpf());

        logger.info("✅ Teste passou! Aluno '{}' salvo com sucesso no H2", alunoSalvo.getNome());

        // Cleanup
        logger.debug("🧹 Limpando dados de teste...");
        alunoRepository.delete(alunoSalvo);
        logger.debug("✅ Dados limpos");
    }

    @Test
    void salvarAlunoBancoH2FalhaCpfInvalido() {
        logger.info("🧪 Testando salvamento com CPF inválido (deve falhar)");

        // Arrange
        Aluno aluno = Aluno.builder()
                .cpf("123") // CPF inválido (menos de 14 caracteres)
                .nome("João da Silva Souza")
                .endereco("Rua Teste, 123, Bairro Exemplo, Cidade, Estado, CEP 12345-678")
                .turma("1001B")
                .nota1(9.0)
                .nota2(8.5)
                .nota3(9.2)
                .build();

        logger.debug("❌ Dados do aluno com CPF inválido:");
        logger.debug("  - CPF: '{}' (tamanho: {} - deveria ser 14)", aluno.getCpf(), aluno.getCpf().length());
        logger.debug("  - Nome: {}", aluno.getNome());
        logger.debug("  - Endereço: {} (tamanho: {})", aluno.getEndereco(), aluno.getEndereco().length());

        // Act & Assert
        logger.info("💾 Tentando salvar aluno com CPF inválido (deve gerar exceção)...");

        TransactionSystemException thrown = assertThrows(TransactionSystemException.class, () -> {
            logger.debug("🔄 Executando save() no repository...");
            alunoRepository.save(aluno);
        });

        logger.info("✅ Verificando se a exceção correta foi lançada...");
        assertThat(thrown).hasRootCauseInstanceOf(ConstraintViolationException.class);

        logger.info("✅ Teste passou! CPF inválido '{}' gerou ConstraintViolationException como esperado", aluno.getCpf());
        logger.debug("🔍 Causa raiz da exceção: {}", thrown.getRootCause().getClass().getSimpleName());
    }

    @Test
    void salvarAlunoBancoH2FalhaEnderecoInvalido() {
        logger.info("🧪 Testando salvamento com endereço inválido (deve falhar)");

        // Arrange
        Aluno aluno = Aluno.builder()
                .cpf("123.456.789-01") // CPF com formato correto
                .nome("João da Silva Souza")
                .endereco("Rua Teste") // Endereço inválido (menos de 25 caracteres)
                .turma("1001B")
                .nota1(9.0)
                .nota2(8.5)
                .nota3(9.2)
                .build();

        logger.debug("❌ Dados do aluno com endereço inválido:");
        logger.debug("  - CPF: {} (tamanho: {} - válido)", aluno.getCpf(), aluno.getCpf().length());
        logger.debug("  - Nome: {}", aluno.getNome());
        logger.debug("  - Endereço: '{}' (tamanho: {} - deveria ser >= 25)", aluno.getEndereco(), aluno.getEndereco().length());

        // Act & Assert
        logger.info("💾 Tentando salvar aluno com endereço inválido (deve gerar exceção)...");

        TransactionSystemException thrown = assertThrows(TransactionSystemException.class, () -> {
            logger.debug("🔄 Executando save() no repository...");
            alunoRepository.save(aluno);
        });

        logger.info("✅ Verificando se a exceção correta foi lançada...");
        assertThat(thrown).hasRootCauseInstanceOf(ConstraintViolationException.class);

        logger.info("✅ Teste passou! Endereço inválido '{}' gerou ConstraintViolationException como esperado", aluno.getEndereco());
        logger.debug("🔍 Causa raiz da exceção: {}", thrown.getRootCause().getClass().getSimpleName());
    }

    @Test
    void testFindApproved() {
        logger.info("🧪 Testando query customizada findApproved()");

        // Arrange
        logger.debug("🧹 Limpando repositório antes do teste...");
        long countAntes = alunoRepository.count();
        alunoRepository.deleteAll();
        logger.debug("✅ Repositório limpo (removidos {} registros)", countAntes);

        // Criando alunos de teste
        logger.debug("📝 Criando alunos de teste...");

        Aluno alunoAprovado = Aluno.builder()
                .cpf("123.456.789-11")
                .nome("Aluno Aprovado")
                .endereco("Rua da Aprovação, 100, Bairro Sucesso, Cidade Teste, Estado Teste")
                .turma("1001A")
                .nota1(8.0)
                .nota2(9.0)
                .nota3(10.0)
                .aprovado("SIM")
                .build();

        Aluno alunoReprovado = Aluno.builder()
                .cpf("123.456.789-22")
                .nome("Aluno Reprovado")
                .endereco("Rua da Reprovação, 200, Bairro Falha, Cidade Teste, Estado Teste")
                .turma("1001A")
                .nota1(4.0)
                .nota2(5.0)
                .nota3(6.0)
                .aprovado("NÃO")
                .build();

        logger.debug("📊 Aluno Aprovado criado:");
        logger.debug("  - CPF: {}, Nome: {}", alunoAprovado.getCpf(), alunoAprovado.getNome());
        logger.debug("  - Notas: [{}, {}, {}] -> Média: {:.2f}",
                alunoAprovado.getNota1(), alunoAprovado.getNota2(), alunoAprovado.getNota3(),
                (alunoAprovado.getNota1() + alunoAprovado.getNota2() + alunoAprovado.getNota3()) / 3);
        logger.debug("  - Status: {}", alunoAprovado.getAprovado());

        logger.debug("📊 Aluno Reprovado criado:");
        logger.debug("  - CPF: {}, Nome: {}", alunoReprovado.getCpf(), alunoReprovado.getNome());
        logger.debug("  - Notas: [{}, {}, {}] -> Média: {:.2f}",
                alunoReprovado.getNota1(), alunoReprovado.getNota2(), alunoReprovado.getNota3(),
                (alunoReprovado.getNota1() + alunoReprovado.getNota2() + alunoReprovado.getNota3()) / 3);
        logger.debug("  - Status: {}", alunoReprovado.getAprovado());

        logger.info("💾 Salvando alunos de teste no banco...");
        alunoRepository.save(alunoAprovado);
        alunoRepository.save(alunoReprovado);
        logger.debug("✅ 2 alunos salvos no banco");

        // Act
        logger.info("🔍 Executando query findApproved()...");
        long startTime = System.currentTimeMillis();
        List<Aluno> aprovados = alunoRepository.findApproved();
        long endTime = System.currentTimeMillis();

        logger.debug("⏱️ Tempo de execução da query: {}ms", (endTime - startTime));
        logger.debug("📊 Resultado da query: {} aluno(s) aprovado(s) encontrado(s)", aprovados.size());

        // Assert
        logger.info("✅ Verificando resultados da query...");
        assertThat(aprovados).isNotNull();
        assertThat(aprovados.size()).isGreaterThanOrEqualTo(1);

        logger.debug("🔍 Procurando aluno aprovado específico na lista...");
        boolean alunoAprovadoEncontrado = aprovados.stream()
                .anyMatch(a -> a.getCpf().equals(alunoAprovado.getCpf()));

        assertThat(alunoAprovadoEncontrado).isTrue();

        logger.debug("📋 Alunos aprovados encontrados:");
        aprovados.forEach(aluno ->
                logger.debug("  - CPF: {}, Nome: {}, Status: {}",
                        aluno.getCpf(), aluno.getNome(), aluno.getAprovado()));

        logger.info("✅ Teste passou! Query findApproved() retornou {} aluno(s) aprovado(s)", aprovados.size());

        // Cleanup
        logger.debug("🧹 Limpando dados de teste...");
        alunoRepository.deleteAll();
        logger.debug("✅ Dados limpos");
    }

    @Test
    void testFindFailedInOneExam() {
        logger.info("🧪 Testando query customizada findFailedInOneExam()");

        // Arrange
        logger.debug("🧹 Limpando repositório antes do teste...");
        long countAntes = alunoRepository.count();
        alunoRepository.deleteAll();
        logger.debug("✅ Repositório limpo (removidos {} registros)", countAntes);

        // Criando alunos de teste
        logger.debug("📝 Criando alunos de teste...");

        Aluno alunoComNotaZero = Aluno.builder()
                .cpf("123.456.789-33")
                .nome("Aluno Com Nota Zero")
                .endereco("Rua das Notas, 300, Bairro Teste, Cidade Teste, Estado Teste")
                .turma("1001B")
                .nota1(8.0)
                .nota2(0.0)  // Nota zero na 2ª avaliação
                .nota3(9.0)
                .aprovado("NÃO")
                .build();

        Aluno alunoComTodasNotas = Aluno.builder()
                .cpf("123.456.789-44")
                .nome("Aluno Com Todas Notas")
                .endereco("Rua das Notas Completas, 400, Bairro Teste, Cidade Teste")
                .turma("1001B")
                .nota1(7.0)
                .nota2(7.5)
                .nota3(8.0)
                .aprovado("SIM")
                .build();

        logger.debug("📊 Aluno com nota zero criado:");
        logger.debug("  - CPF: {}, Nome: {}", alunoComNotaZero.getCpf(), alunoComNotaZero.getNome());
        logger.debug("  - Notas: [{}, {}, {}] (nota zero na posição 2)",
                alunoComNotaZero.getNota1(), alunoComNotaZero.getNota2(), alunoComNotaZero.getNota3());
        logger.debug("  - Status: {}", alunoComNotaZero.getAprovado());

        logger.debug("📊 Aluno com todas as notas criado:");
        logger.debug("  - CPF: {}, Nome: {}", alunoComTodasNotas.getCpf(), alunoComTodasNotas.getNome());
        logger.debug("  - Notas: [{}, {}, {}] (todas > 0)",
                alunoComTodasNotas.getNota1(), alunoComTodasNotas.getNota2(), alunoComTodasNotas.getNota3());
        logger.debug("  - Status: {}", alunoComTodasNotas.getAprovado());

        logger.info("💾 Salvando alunos de teste no banco...");
        alunoRepository.save(alunoComNotaZero);
        alunoRepository.save(alunoComTodasNotas);
        logger.debug("✅ 2 alunos salvos no banco");

        // Act
        logger.info("🔍 Executando query findFailedInOneExam()...");
        long startTime = System.currentTimeMillis();
        List<Aluno> alunosReprovadosUmaProva = alunoRepository.findFailedInOneExam();
        long endTime = System.currentTimeMillis();

        logger.debug("⏱️ Tempo de execução da query: {}ms", (endTime - startTime));
        logger.debug("📊 Resultado da query: {} aluno(s) reprovado(s) em uma prova encontrado(s)",
                alunosReprovadosUmaProva.size());

        // Assert
        logger.info("✅ Verificando resultados da query...");
        assertThat(alunosReprovadosUmaProva).isNotNull();
        assertThat(alunosReprovadosUmaProva.size()).isGreaterThanOrEqualTo(1);

        logger.debug("🔍 Procurando aluno com nota zero específico na lista...");
        boolean alunoComNotaZeroEncontrado = alunosReprovadosUmaProva.stream()
                .anyMatch(a -> a.getCpf().equals(alunoComNotaZero.getCpf()));

        assertThat(alunoComNotaZeroEncontrado).isTrue();

        logger.debug("📋 Alunos reprovados em uma prova encontrados:");
        alunosReprovadosUmaProva.forEach(aluno -> {
            int provasComNota = contarProvasComNota(aluno);
            logger.debug("  - CPF: {}, Nome: {}, Provas com nota: {}/3",
                    aluno.getCpf(), aluno.getNome(), provasComNota);
        });

        logger.info("✅ Teste passou! Query findFailedInOneExam() retornou {} aluno(s)",
                alunosReprovadosUmaProva.size());

        // Cleanup
        logger.debug("🧹 Limpando dados de teste...");
        alunoRepository.deleteAll();
        logger.debug("✅ Dados limpos");
    }

    /**
     * Método auxiliar para contar quantas provas têm nota > 0
     */
    private int contarProvasComNota(Aluno aluno) {
        int count = 0;
        if (aluno.getNota1() != null && aluno.getNota1() > 0) count++;
        if (aluno.getNota2() != null && aluno.getNota2() > 0) count++;
        if (aluno.getNota3() != null && aluno.getNota3() > 0) count++;
        logger.debug("    Detalhes: Nota1={} ({}), Nota2={} ({}), Nota3={} ({})",
                aluno.getNota1(), aluno.getNota1() > 0 ? "✓" : "✗",
                aluno.getNota2(), aluno.getNota2() > 0 ? "✓" : "✗",
                aluno.getNota3(), aluno.getNota3() > 0 ? "✓" : "✗");
        return count;
    }
}