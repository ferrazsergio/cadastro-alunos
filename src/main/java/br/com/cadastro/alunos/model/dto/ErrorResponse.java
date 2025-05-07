package br.com.cadastro.alunos.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Resposta de erro")
public class ErrorResponse {

    @Schema(description = "Título do erro", example = "Erro de validação")
    private String title;

    @Schema(description = "Detalhes do erro", example = "O campo CPF deve ter 13 caracteres")
    private String details;

    @Schema(description = "Data e hora do erro", example = "2025-05-07T17:26:57")
    private LocalDateTime timestamp;
}