package br.com.cadastro.alunos.model.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Builder
@Schema(description = "MODELO ALUNOS")
@Table(name = "ALUNOS")
public class Aluno {

    @Id
    @Column(unique = true, name = "CPF")
    @NotBlank(message = "O CPF do aluno não pode ser nulo ou vazio")
    @Schema(description = "CPF do aluno ", example = "000.000.00-00")
    @Size(min = 13, max = 14, message = "O campo CPF deve ter 13 caracteres")
    private String cpf;


    @Column(name = "NOME")
    @Schema(description = "Nome do aluno", example = "João Da Silva Souza")
    @NotBlank(message = "O nome do aluno não pode ser nulo ou vazio")
    @Size(min = 10, max = 40, message = "O campo NOME_ALUNO deve ter no minimo 10 caracteres e no maximo 40 ")
    private String nome;


    @Column(name = "ENDERECO")
    @Schema(description = "Endereço do aluno", example = "Rua avelar, n 34, casa 02")
    @NotBlank(message = "O endereço do aluno não pode ser nulo ou vazio")
    @Size(min = 25,max = 100, message = "O campo ENDERECO_ALUNO deve ter no minimo 25 caracteres e no maximo 100 ")
    private String endereco;

    @Column(name = "TURMA")
    @Schema(description = "Turma", example = "1001B")
    @NotBlank(message = "A turma do aluno não pode ser nula ou vazia")
    @Size(min = 4,max = 5, message = "O campo TURMA deve ter no minimo 4 caracteres e no maximo 5 ")
    private String turma;


    @Column(name = "NOTA_1", columnDefinition = "DECIMAL(3, 1)")
    @Schema(description = "Nota da primeira avaliação do aluno", example = "10")
    @NotNull(message = "A nota da primeira avaliação do aluno não pode ser nula ou vazia")
    @DecimalMin(value = "0.0", message = "O campo NOTA_1 deve ser no mínimo 0.0")
    @DecimalMax(value = "10.0", message = "O campo NOTA_1 deve ser no máximo 10.0")
    private Double nota1;

    @Column(name = "NOTA_2" , columnDefinition = "DECIMAL(3,1)")
    @Schema(description = "Nota da segunda avaliação do aluno", example = "10")
    @NotNull(message = "A nota da primeira avaliação do aluno não pode ser nula ou vazia")
    @DecimalMin(value = "0.0", message = "O campo NOTA_2 deve ser no mínimo 0.0")
    @DecimalMax(value = "10.0", message = "O campo NOTA_2 deve ser no máximo 10.0")
    private Double nota2;

    @Column(name = "NOTA_3",columnDefinition = "DECIMAL(3,1)")
    @Schema(description = "Nota da terceira avaliação do aluno", example = "10")
    @NotNull(message = "A nota da primeira avaliação do aluno não pode ser nula ou vazia")
    @DecimalMin(value = "0.0", message = "O campo NOTA_3 deve ser no mínimo 0.0")
    @DecimalMax(value = "10.0", message = "O campo NOTA_3 deve ser no máximo 10.0")
    private Double nota3;

    @Schema(description = "Coluna para mostrar se o aluno esta aprovado ou reprovado" , hidden = true)
    @Column(name = "APROVADO")
    private String aprovado;
    
  
	public Aluno() {
		super();
	}
	
	public Aluno(
			@NotBlank(message = "O CPF do aluno não pode ser nulo ou vazio") @Size(min = 13, max = 13, message = "O campo CPF deve ter 13 caracteres") String cpf,
			@NotBlank(message = "O nome do aluno não pode ser nulo ou vazio") @Size(min = 10, max = 40, message = "O campo NOME_ALUNO deve ter no minimo 10 caracteres e no maximo 40 ") String nome,
			@NotBlank(message = "O endereço do aluno não pode ser nulo ou vazio") @Size(min = 25, max = 100, message = "O campo ENDERECO_ALUNO deve ter no minimo 25 caracteres e no maximo 100 ") String endereco,
			@NotBlank(message = "A turma do aluno não pode ser nula ou vazia") @Size(min = 4, max = 5, message = "O campo TURMA deve ter no minimo 4 caracteres e no maximo 5 ") String turma,
			@NotNull(message = "A nota da primeira avaliação do aluno não pode ser nula ou vazia") @DecimalMin(value = "0.0", message = "O campo NOTA_1 deve ser no mínimo 0.0") @DecimalMax(value = "10.0", message = "O campo NOTA_1 deve ser no máximo 10.0") Double nota1,
			@NotNull(message = "A nota da primeira avaliação do aluno não pode ser nula ou vazia") @DecimalMin(value = "0.0", message = "O campo NOTA_2 deve ser no mínimo 0.0") @DecimalMax(value = "10.0", message = "O campo NOTA_2 deve ser no máximo 10.0") Double nota2,
			@NotNull(message = "A nota da primeira avaliação do aluno não pode ser nula ou vazia") @DecimalMin(value = "0.0", message = "O campo NOTA_3 deve ser no mínimo 0.0") @DecimalMax(value = "10.0", message = "O campo NOTA_3 deve ser no máximo 10.0") Double nota3,
			String aprovado) {
		super();
		this.cpf = cpf;
		this.nome = nome;
		this.endereco = endereco;
		this.turma = turma;
		this.nota1 = nota1;
		this.nota2 = nota2;
		this.nota3 = nota3;
		this.aprovado = aprovado;
	}



	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getEndereco() {
		return endereco;
	}

	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}

	public String getTurma() {
		return turma;
	}

	public void setTurma(String turma) {
		this.turma = turma;
	}

	public Double getNota1() {
		return nota1;
	}

	public void setNota1(Double nota1) {
		this.nota1 = nota1;
	}

	public Double getNota2() {
		return nota2;
	}

	public void setNota2(Double nota2) {
		this.nota2 = nota2;
	}

	public Double getNota3() {
		return nota3;
	}

	public void setNota3(Double nota3) {
		this.nota3 = nota3;
	}

	public String getAprovado() {
		return aprovado;
	}

	public void setAprovado(String aprovado) {
		this.aprovado = aprovado;
	}

	@Override
	public String toString() {
		return "Aluno [cpf=" + cpf + ", nome=" + nome + ", endereco=" + endereco + ", turma=" + turma + ", nota1="
				+ nota1 + ", nota2=" + nota2 + ", nota3=" + nota3 + ", aprovado=" + aprovado + "]";
	}
}
