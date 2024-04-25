package br.com.cadastro.alunos.model.services;

import br.com.cadastro.alunos.model.entities.Aluno;
import br.com.cadastro.alunos.model.repository.AlunoRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;


import java.util.List;

@Component
@Service
public class AlunoService {

    private final AlunoRepository alunoRepository;
    private static final Logger logger = LogManager.getLogger(AlunoService.class);

    @Autowired
    public AlunoService(AlunoRepository alunoRepository) {
        this.alunoRepository = alunoRepository;
    }



    public List<Aluno> listarAlunos() {
        // Lógica para listar todos os alunos
        return alunoRepository.findAll();
    }

    public Aluno incluirAluno(Aluno aluno) {
        // Verificar se o CPF já está cadastrado
        if (alunoRepository.existsById(aluno.getCpf())) {
            throw new IllegalArgumentException("CPF já cadastrado");
        }

        // Verificar se o aluno já está cadastrado em outra turma
        if (alunoRepository.existsByCpfAndTurmaNot(aluno.getCpf(),aluno.getTurma())) {
            throw new IllegalArgumentException("Aluno já cadastrado em outra turma");
        }

        // Salvando o aluno no banco de dados
        Aluno alunoSalvo = alunoRepository.save(aluno);

        // Log dos dados do aluno cadastrado
        logger.info("Aluno cadastrado: {}", alunoSalvo);
        // Avaliar os alunos após a inclusão
        avaliarAlunos();

        return alunoSalvo;

    }


    public Aluno alterarAluno(String cpf, Aluno aluno) {
        // Verificar se o aluno com o CPF fornecido existe
        Aluno alunoExistente = alunoRepository.findByCpf(cpf);
        if (alunoExistente == null) {
            throw new IllegalArgumentException("Aluno com CPF " + cpf + " não encontrado");
        }

        // Atualizar os dados do aluno com base nos dados recebidos
        alunoExistente.setNome(aluno.getNome());
        alunoExistente.setEndereco(aluno.getEndereco());
        alunoExistente.setTurma(aluno.getTurma());
        alunoExistente.setNota1(aluno.getNota1());
        alunoExistente.setNota2(aluno.getNota2());
        alunoExistente.setNota3(aluno.getNota3());

        // Salvar as alterações no banco de dados
        Aluno alunoAlterado = alunoRepository.save(alunoExistente);
        // Log dos dados do aluno alterado
        logger.info("Aluno com CPF {} alterado: {}", cpf, alunoAlterado);
        // Avaliar os alunos após a alteração
        avaliarAlunos();

        return alunoAlterado;
    }

    public void excluirAluno(String cpf) {
        // Lógica para excluir o aluno
        alunoRepository.deleteById(cpf);
        // Log do CPF do aluno excluído
        logger.info("Aluno com CPF {} excluído", cpf);
        // Avaliar os alunos após a exclusão
        avaliarAlunos();

    }


    public void avaliarAlunos() {
        List<Aluno> alunos = alunoRepository.findAll();
        for (Aluno aluno : alunos) {
            double media = calcularMedia(aluno);
            // Define se o aluno está aprovado ou reprovado com base na média
            aluno.setAprovado(media >= 7.0 ? "SIM" : "NÃO");
            alunoRepository.save(aluno);
        }
    }

    private double calcularMedia(Aluno aluno) {
        return (aluno.getNota1() + aluno.getNota2() + aluno.getNota3()) / 3.0;
    }
}
