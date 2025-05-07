package br.com.cadastro.alunos.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados do aluno para API")
public class AlunoDTO {

    @Schema(description = "CPF do aluno", example = "000.000.000-00")
    private String cpf;

    @Schema(description = "Nome do aluno", example = "João Da Silva Souza")
    private String nome;

    @Schema(description = "Turma do aluno", example = "1001B")
    private String turma;

    @Schema(description = "Média das notas do aluno", example = "8.50")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "#.00")
    private Double media;

    @Schema(description = "Situação do aluno (APROVADO/REPROVADO)", example = "APROVADO")
    private String situacao;

    // Opcional: incluir outras informações relevantes para a API
    @Schema(description = "Nota da primeira avaliação", example = "9.0")
    private Double nota1;

    @Schema(description = "Nota da segunda avaliação", example = "8.0")
    private Double nota2;

    @Schema(description = "Nota da terceira avaliação", example = "8.5")
    private Double nota3;
}