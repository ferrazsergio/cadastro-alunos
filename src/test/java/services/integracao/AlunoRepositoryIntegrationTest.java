package services.integracao;

import br.com.cadastro.alunos.model.entities.Aluno;
import br.com.cadastro.alunos.model.repository.AlunoRepository;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.TransactionSystemException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Tag("integracao")
@SpringBootTest(classes = br.com.cadastro.alunos.CadastroAlunosApplication.class)
@ActiveProfiles("test")  // Para carregar o application-test.properties
public class AlunoRepositoryIntegrationTest {

    @Autowired
    private AlunoRepository alunoRepository;
    private static final Logger logger = LoggerFactory.getLogger(AlunoRepositoryIntegrationTest.class);

    @Test
    void salvarAlunoBancoH2() {
        Aluno aluno = Aluno.builder()
                .cpf("123.456.789-01") // CPF com 13 caracteres (formato correto)
                .nome("João da Silva Souza")
                .endereco("Rua Teste, 123, Bairro Exemplo, Cidade, Estado, CEP 12345-678")
                .turma("1001B")
                .nota1(9.0)
                .nota2(8.5)
                .nota3(9.2)
                .aprovado("SIM")  // Adicionando campo aprovado
                .build();

        logger.info("Dados do aluno que serão inseridos: {}", aluno);

        Aluno alunoSalvo = alunoRepository.save(aluno);

        logger.info("Dados do aluno salvos no banco de dados: {}", alunoSalvo);

        assertThat(alunoSalvo).isNotNull();
        assertThat(alunoSalvo.getCpf()).isEqualTo(aluno.getCpf());
    }

    @Test
    void salvarAlunoBancoH2FalhaCpfInvalido() {
        Aluno aluno = Aluno.builder()
                .cpf("123") // CPF inválido (menos de 13 caracteres)
                .nome("João da Silva Souza")
                .endereco("Rua Teste, 123, Bairro Exemplo, Cidade, Estado, CEP 12345-678")
                .turma("1001B")
                .nota1(9.0)
                .nota2(8.5)
                .nota3(9.2)
                .build();

        logger.info("Dados do aluno que serão inseridos (CPF inválido): {}", aluno);

        // Verifica se a exceção de validação é lançada
        TransactionSystemException thrown = assertThrows(TransactionSystemException.class, () -> {
            alunoRepository.save(aluno);
        });

        assertThat(thrown).hasRootCauseInstanceOf(ConstraintViolationException.class);

        logger.info("Teste de falha: CPF inválido lançou ConstraintViolationException, como esperado.");
    }

    @Test
    void salvarAlunoBancoH2FalhaEnderecoInvalido() {
        Aluno aluno = Aluno.builder()
                .cpf("123.456.789-01") // CPF com formato correto
                .nome("João da Silva Souza")
                .endereco("Rua Teste") // Endereço inválido (menos de 25 caracteres)
                .turma("1001B")
                .nota1(9.0)
                .nota2(8.5)
                .nota3(9.2)
                .build();

        logger.info("Dados do aluno que serão inseridos (Endereço inválido): {}", aluno);

        // Verifica se a exceção de validação é lançada
        TransactionSystemException thrown = assertThrows(TransactionSystemException.class, () -> {
            alunoRepository.save(aluno);
        });

        assertThat(thrown).hasRootCauseInstanceOf(ConstraintViolationException.class);

        logger.info("Teste de falha: Endereço inválido lançou ConstraintViolationException, como esperado.");
    }

    // Novos testes para as queries otimizadas

    @Test
    void testFindApproved() {
        // Limpa o repositório
        alunoRepository.deleteAll();

        // Adiciona um aluno aprovado
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

        // Adiciona um aluno reprovado
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

        alunoRepository.save(alunoAprovado);
        alunoRepository.save(alunoReprovado);

        // Testa o método
        List<Aluno> aprovados = alunoRepository.findApproved();

        assertThat(aprovados).isNotNull();
        assertThat(aprovados.size()).isGreaterThanOrEqualTo(1);

        // Verifica se o aluno aprovado está na lista
        boolean alunoAprovadoEncontrado = aprovados.stream()
                .anyMatch(a -> a.getCpf().equals(alunoAprovado.getCpf()));

        assertThat(alunoAprovadoEncontrado).isTrue();
    }

    @Test
    void testFindFailedInOneExam() {
        // Limpa o repositório
        alunoRepository.deleteAll();

        // Adiciona um aluno com nota zero na 2ª avaliação
        Aluno alunoComNotaZero = Aluno.builder()
                .cpf("123.456.789-33")
                .nome("Aluno Com Nota Zero")
                .endereco("Rua das Notas, 300, Bairro Teste, Cidade Teste, Estado Teste")
                .turma("1001B")
                .nota1(8.0)
                .nota2(0.0)
                .nota3(9.0)
                .aprovado("NÃO")
                .build();

        // Adiciona um aluno com todas as notas
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

        alunoRepository.save(alunoComNotaZero);
        alunoRepository.save(alunoComTodasNotas);

        // Testa o método
        List<Aluno> alunosReprovadosUmaProva = alunoRepository.findFailedInOneExam();

        assertThat(alunosReprovadosUmaProva).isNotNull();
        assertThat(alunosReprovadosUmaProva.size()).isGreaterThanOrEqualTo(1);

        // Verifica se o aluno com nota zero está na lista
        boolean alunoComNotaZeroEncontrado = alunosReprovadosUmaProva.stream()
                .anyMatch(a -> a.getCpf().equals(alunoComNotaZero.getCpf()));

        assertThat(alunoComNotaZeroEncontrado).isTrue();
    }
}