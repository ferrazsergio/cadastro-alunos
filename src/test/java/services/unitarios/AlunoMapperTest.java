package services.unitarios;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import br.com.cadastro.alunos.model.dto.AlunoDTO;
import br.com.cadastro.alunos.model.entities.Aluno;
import br.com.cadastro.alunos.model.mapper.AlunoMapper;

@Tag("unitario")
class AlunoMapperTest {

    private final AlunoMapper alunoMapper = new AlunoMapper();

    @Test
    void deveMappearAlunoParaDTOComSucesso() {
        // Arrange
        Aluno aluno = new Aluno(
                "123.456.789-09",
                "Carlos",
                "Rua A, número 123, Bairro Exemplo, Cidade Teste",
                "Turma 1",
                9.0,
                8.0,
                7.0,
                "SIM"
        );

        // Act
        AlunoDTO dto = alunoMapper.toDTO(aluno);

        // Assert
        assertNotNull(dto);
        assertEquals("123.456.789-09", dto.getCpf());
        assertEquals("Carlos", dto.getNome());
        assertEquals("Turma 1", dto.getTurma());
        assertEquals(8.0, dto.getMedia(), 0.01); // Média com 2 casas decimais
        assertEquals("APROVADO", dto.getSituacao());
        assertEquals(9.0, dto.getNota1());
        assertEquals(8.0, dto.getNota2());
        assertEquals(7.0, dto.getNota3());
    }

    @Test
    void deveRetornarNullQuandoAlunoForNull() {
        AlunoDTO dto = alunoMapper.toDTO(null);
        assertNull(dto);
    }

    @Test
    void deveCalcularMediaeStatusParaAlunoAprovado() {
        // Arrange
        Aluno aluno = new Aluno(
                "123.456.789-09",
                "Carlos",
                "Rua A, número 123, Bairro Exemplo, Cidade Teste",
                "Turma 1",
                7.5,
                7.0,
                7.0,
                null // Status será calculado pelo mapper
        );

        // Act
        AlunoDTO dto = alunoMapper.toDTO(aluno);

        // Assert
        assertEquals(7.17, dto.getMedia(), 0.01); // (7.5 + 7.0 + 7.0) / 3 = 7.17
        assertEquals("APROVADO", dto.getSituacao());
    }

    @Test
    void deveCalcularMediaEStatusParaAlunoReprovado() {
        // Arrange
        Aluno aluno = new Aluno(
                "123.456.789-09",
                "Carlos",
                "Rua A, número 123, Bairro Exemplo, Cidade Teste",
                "Turma 1",
                6.0,
                6.0,
                6.0,
                null // Status será calculado pelo mapper
        );

        // Act
        AlunoDTO dto = alunoMapper.toDTO(aluno);

        // Assert
        assertEquals(6.0, dto.getMedia(), 0.01);
        assertEquals("REPROVADO", dto.getSituacao());
    }

    @Test
    void deveFormatarMediaComDuasCasasDecimais() {
        // Arrange
        Aluno aluno = new Aluno(
                "123.456.789-09",
                "Carlos",
                "Rua A, número 123, Bairro Exemplo, Cidade Teste",
                "Turma 1",
                5.0,
                10.0,
                10.0,
                null
        );

        // Act
        AlunoDTO dto = alunoMapper.toDTO(aluno);

        // Assert
        // (5.0 + 10.0 + 10.0) / 3 = 8.333333
        assertEquals(8.33, dto.getMedia(), 0.01); // Verifica formatação com 2 casas
    }
}