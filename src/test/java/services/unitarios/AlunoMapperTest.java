package services.unitarios;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import br.com.cadastro.alunos.model.dto.AlunoDTO;
import br.com.cadastro.alunos.model.entities.Aluno;
import br.com.cadastro.alunos.model.mapper.AlunoMapper;

@Tag("unitario")
class AlunoMapperTest {

    private static final Logger logger = LogManager.getLogger(AlunoMapperTest.class);
    private final AlunoMapper alunoMapper = new AlunoMapper();

    @BeforeEach
    void setUp() {
        logger.info("=== Iniciando teste do AlunoMapper ===");
    }

    @Test
    void deveMappearAlunoParaDTOComSucesso() {
        logger.info("Testando mapeamento de aluno aprovado com campo 'Sim'");

        // Arrange
        Aluno aluno = new Aluno(
                "123.456.789-09",
                "Carlos",
                "Rua A, número 123, Bairro Exemplo, Cidade Teste",
                "Turma 1",
                9.0,
                8.0,
                7.0,
                "Sim"
        );

        logger.debug("Aluno criado: CPF={}, Nome={}, Notas=[{}, {}, {}], Aprovado={}",
                aluno.getCpf(), aluno.getNome(), aluno.getNota1(), aluno.getNota2(), aluno.getNota3(), aluno.getAprovado());

        // Act
        logger.info("Executando mapeamento para DTO...");
        AlunoDTO dto = alunoMapper.toDTO(aluno);

        // Assert
        logger.info("Verificando resultado do mapeamento...");
        assertNotNull(dto);
        assertEquals("123.456.789-09", dto.getCpf());
        assertEquals("Carlos", dto.getNome());
        assertEquals("Turma 1", dto.getTurma());
        assertEquals(8.0, dto.getMedia(), 0.01); // Média com 2 casas decimais
        assertEquals("APROVADO", dto.getSituacao());
        assertEquals(9.0, dto.getNota1());
        assertEquals(8.0, dto.getNota2());
        assertEquals(7.0, dto.getNota3());

        logger.info("✅ Teste passou! DTO mapeado: CPF={}, Nome={}, Média={}, Situação={}",
                dto.getCpf(), dto.getNome(), dto.getMedia(), dto.getSituacao());
    }

    @Test
    void deveRetornarNullQuandoAlunoForNull() {
        logger.info("Testando mapeamento com aluno null");

        // Act
        logger.info("Executando mapeamento com entrada null...");
        AlunoDTO dto = alunoMapper.toDTO(null);

        // Assert
        logger.info("Verificando se retorna null...");
        assertNull(dto);

        logger.info("✅ Teste passou! Retornou null como esperado");
    }

    @Test
    void deveCalcularMediaeStatusParaAlunoAprovado() {
        logger.info("Testando cálculo de média e status para aluno aprovado (campo aprovado = null)");

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

        logger.debug("Aluno criado: Notas=[{}, {}, {}], Campo aprovado=null",
                aluno.getNota1(), aluno.getNota2(), aluno.getNota3());

        double mediaEsperada = (7.5 + 7.0 + 7.0) / 3;
        logger.debug("Média esperada: {}", mediaEsperada);

        // Act
        logger.info("Executando mapeamento com cálculo automático de situação...");
        AlunoDTO dto = alunoMapper.toDTO(aluno);

        // Assert
        logger.info("Verificando média e situação calculadas...");
        assertEquals(7.17, dto.getMedia(), 0.01); // (7.5 + 7.0 + 7.0) / 3 = 7.17
        assertEquals("APROVADO", dto.getSituacao());

        logger.info("✅ Teste passou! Média calculada: {}, Situação: {}", dto.getMedia(), dto.getSituacao());
    }

    @Test
    void deveCalcularMediaEStatusParaAlunoReprovado() {
        logger.info("Testando cálculo de média e status para aluno reprovado (campo aprovado = null)");

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

        logger.debug("Aluno criado: Notas=[{}, {}, {}], Campo aprovado=null",
                aluno.getNota1(), aluno.getNota2(), aluno.getNota3());

        double mediaEsperada = (6.0 + 6.0 + 6.0) / 3;
        logger.debug("Média esperada: {} (≤ 7.0 = REPROVADO)", mediaEsperada);

        // Act
        logger.info("Executando mapeamento com cálculo automático de situação...");
        AlunoDTO dto = alunoMapper.toDTO(aluno);

        // Assert
        logger.info("Verificando média e situação calculadas...");
        assertEquals(6.0, dto.getMedia(), 0.01);
        assertEquals("REPROVADO", dto.getSituacao());

        logger.info("✅ Teste passou! Média calculada: {}, Situação: {}", dto.getMedia(), dto.getSituacao());
    }

    @Test
    void deveFormatarMediaComDuasCasasDecimais() {
        logger.info("Testando formatação de média com duas casas decimais");

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

        logger.debug("Aluno criado: Notas=[{}, {}, {}]", aluno.getNota1(), aluno.getNota2(), aluno.getNota3());

        double mediaCalculada = (5.0 + 10.0 + 10.0) / 3;
        logger.debug("Média bruta calculada: {} (deve ser formatada para 8.33)", mediaCalculada);

        // Act
        logger.info("Executando mapeamento com formatação de média...");
        AlunoDTO dto = alunoMapper.toDTO(aluno);

        // Assert
        logger.info("Verificando formatação da média...");
        // (5.0 + 10.0 + 10.0) / 3 = 8.333333
        assertEquals(8.33, dto.getMedia(), 0.01); // Verifica formatação com 2 casas

        logger.info("✅ Teste passou! Média formatada: {} (era {}, formatada para 2 casas decimais)",
                dto.getMedia(), mediaCalculada);
    }

    @Test
    void deveMappearAlunoReprovadoParaDTOComSucesso() {
        logger.info("Testando mapeamento de aluno reprovado com campo 'Não'");

        // Arrange
        Aluno aluno = new Aluno(
                "123.456.789-09",
                "Ana",
                "Rua B, número 456, Bairro Exemplo, Cidade Teste",
                "Turma 2",
                5.0,
                6.0,
                4.0,
                "Não"  // Aluno reprovado
        );

        logger.debug("Aluno criado: CPF={}, Nome={}, Notas=[{}, {}, {}], Aprovado={}",
                aluno.getCpf(), aluno.getNome(), aluno.getNota1(), aluno.getNota2(), aluno.getNota3(), aluno.getAprovado());

        // Act
        logger.info("Executando mapeamento para DTO...");
        AlunoDTO dto = alunoMapper.toDTO(aluno);

        // Assert
        logger.info("Verificando resultado do mapeamento...");
        assertNotNull(dto);
        assertEquals("123.456.789-09", dto.getCpf());
        assertEquals("Ana", dto.getNome());
        assertEquals("Turma 2", dto.getTurma());
        assertEquals(5.0, dto.getMedia(), 0.01);
        assertEquals("REPROVADO", dto.getSituacao());
        assertEquals(5.0, dto.getNota1());
        assertEquals(6.0, dto.getNota2());
        assertEquals(4.0, dto.getNota3());

        logger.info("✅ Teste passou! DTO mapeado: CPF={}, Nome={}, Média={}, Situação={}",
                dto.getCpf(), dto.getNome(), dto.getMedia(), dto.getSituacao());
    }

    @Test
    void deveMappearAlunoComAPROVADOMaiusculoParaDTOComSucesso() {
        logger.info("Testando mapeamento de aluno com campo 'APROVADO' (todo maiúsculo)");

        Aluno aluno = new Aluno(
                "123.456.789-09",
                "João",
                "Rua C, número 789, Bairro Exemplo, Cidade Teste",
                "Turma 3",
                9.0,
                8.0,
                7.0,
                "APROVADO"  // Valor inconsistente que existe no banco
        );

        logger.debug("Aluno criado: CPF={}, Nome={}, Aprovado='{}' (valor inconsistente do banco)",
                aluno.getCpf(), aluno.getNome(), aluno.getAprovado());

        // Act
        logger.info("Executando mapeamento com valor inconsistente...");
        AlunoDTO dto = alunoMapper.toDTO(aluno);

        // Assert
        logger.info("Verificando se o valor inconsistente é tratado corretamente...");
        assertNotNull(dto);
        assertEquals("APROVADO", dto.getSituacao());

        logger.info("✅ Teste passou! Valor 'APROVADO' mapeado corretamente para situação: {}", dto.getSituacao());
    }

    @Test
    void deveMappearAlunoSemCampoAprovadoUsandoMedia() {
        logger.info("Testando mapeamento de aluno sem campo aprovado, usando média > 7.0");

        // Arrange - testando quando o campo aprovado é null
        Aluno aluno = new Aluno(
                "123.456.789-09",
                "Pedro",
                "Rua D, número 101, Bairro Exemplo, Cidade Teste",
                "Turma 4",
                8.0,
                9.0,
                7.5,
                null  // Campo aprovado é null
        );

        logger.debug("Aluno criado: Notas=[{}, {}, {}], Campo aprovado=null",
                aluno.getNota1(), aluno.getNota2(), aluno.getNota3());

        double mediaCalculada = (8.0 + 9.0 + 7.5) / 3;
        logger.debug("Média calculada: {} (> 7.0, deve ser APROVADO)", mediaCalculada);

        // Act
        logger.info("Executando mapeamento com cálculo baseado na média...");
        AlunoDTO dto = alunoMapper.toDTO(aluno);

        // Assert
        logger.info("Verificando se a situação foi calculada corretamente pela média...");
        assertNotNull(dto);
        assertEquals("APROVADO", dto.getSituacao()); // Média é 8.17, > 7.0

        logger.info("✅ Teste passou! Situação calculada pela média: {} (média: {})",
                dto.getSituacao(), dto.getMedia());
    }

    @Test
    void deveMappearAlunoSemCampoAprovadoUsandoMediaReprovado() {
        logger.info("Testando mapeamento de aluno sem campo aprovado, usando média ≤ 7.0");

        // Arrange - testando quando o campo aprovado é null e média < 7
        Aluno aluno = new Aluno(
                "123.456.789-09",
                "Maria",
                "Rua E, número 202, Bairro Exemplo, Cidade Teste",
                "Turma 5",
                5.0,
                6.0,
                4.0,
                null  // Campo aprovado é null
        );

        logger.debug("Aluno criado: Notas=[{}, {}, {}], Campo aprovado=null",
                aluno.getNota1(), aluno.getNota2(), aluno.getNota3());

        double mediaCalculada = (5.0 + 6.0 + 4.0) / 3;
        logger.debug("Média calculada: {} (≤ 7.0, deve ser REPROVADO)", mediaCalculada);

        // Act
        logger.info("Executando mapeamento com cálculo baseado na média...");
        AlunoDTO dto = alunoMapper.toDTO(aluno);

        // Assert
        logger.info("Verificando se a situação foi calculada corretamente pela média...");
        assertNotNull(dto);
        assertEquals("REPROVADO", dto.getSituacao()); // Média é 5.0, <= 7.0

        logger.info("✅ Teste passou! Situação calculada pela média: {} (média: {})",
                dto.getSituacao(), dto.getMedia());
    }
}