package br.com.cadastro.alunos.model.mapper;

import br.com.cadastro.alunos.model.dto.AlunoDTO;
import br.com.cadastro.alunos.model.entities.Aluno;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
@SuppressWarnings("java:S1192")
public class AlunoMapper {

    /**
     * Converte uma entidade Aluno para AlunoDTO
     */
    public AlunoDTO toDTO(Aluno aluno) {
        if (aluno == null) {
            return null;
        }

        // Calcula a média com precisão de duas casas decimais
        double mediaCalculada = calcularMedia(aluno);
        double mediaFormatada = formatarMedia(mediaCalculada);

        // Converte os valores do banco para o DTO
        String situacao;
        if (aluno.getAprovado() != null) {
            // Considerar todos os possíveis valores de aprovado
            if ("Sim".equals(aluno.getAprovado()) || "APROVADO".equals(aluno.getAprovado())) {
                situacao = "APROVADO";
            } else {
                situacao = "REPROVADO";
            }
        } else {
            situacao = mediaCalculada >= 7.0 ? "APROVADO" : "REPROVADO";
        }

        return AlunoDTO.builder()
                .cpf(aluno.getCpf())
                .nome(aluno.getNome())
                .turma(aluno.getTurma())
                .media(mediaFormatada)
                .situacao(situacao)
                .nota1(aluno.getNota1())
                .nota2(aluno.getNota2())
                .nota3(aluno.getNota3())
                .build();
    }

    /**
     * Converte um DTO em entidade e atualiza o campo aprovado
     */
    public Aluno toEntity(AlunoDTO dto) {
        if (dto == null) {
            return null;
        }

        Aluno aluno = Aluno.builder()
                .cpf(dto.getCpf())
                .nome(dto.getNome())
                .turma(dto.getTurma())
                .nota1(dto.getNota1())
                .nota2(dto.getNota2())
                .nota3(dto.getNota3())
                .build();

        // Atualiza o campo aprovado com base na situação do DTO
        aluno.setAprovado(dto.getSituacao());

        return aluno;
    }

    /**
     * Calcula a média das notas do aluno
     */
    private double calcularMedia(Aluno aluno) {
        return (aluno.getNota1() + aluno.getNota2() + aluno.getNota3()) / 3.0;
    }

    /**
     * Formata a média para duas casas decimais
     */
    private double formatarMedia(double media) {
        // Corrigido para usar String em vez de double no construtor do BigDecimal
        BigDecimal bd = new BigDecimal(String.valueOf(media));
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}