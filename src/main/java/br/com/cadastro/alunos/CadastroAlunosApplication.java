package br.com.cadastro.alunos;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "br.com.cadastro.alunos.model.repository")
@OpenAPIDefinition(info = @Info(
		title = "Cadastro de alunos",
		version = "2.0",
		description = "Este é um projeto exemplo de cadastro de alunos com banco em memoria H2 utilizando o serviço RESTful."
))
public class CadastroAlunosApplication {

	public static void main(String[] args) {
		SpringApplication.run(CadastroAlunosApplication.class, args);
	}

}
