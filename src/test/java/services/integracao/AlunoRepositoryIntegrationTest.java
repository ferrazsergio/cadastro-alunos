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
                .cpf("12345678901234") // CPF com 14 caracteres
                .nome("João da Silva Souza")
                .endereco("Rua Teste, 123, Bairro Exemplo, Cidade, Estado, CEP 12345-678")
                .turma("1001B")
                .nota1(9.0)
                .nota2(8.5)
                .nota3(9.2)
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
                .cpf("123") // CPF inválido (menos de 14 caracteres)
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
                .cpf("12345678901234") // CPF com 14 caracteres
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
}