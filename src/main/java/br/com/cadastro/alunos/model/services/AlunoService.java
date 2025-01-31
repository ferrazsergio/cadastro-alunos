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
        logger.info("Listando todos os alunos");
        List<Aluno> alunos = alunoRepository.findAll();
        logger.info("Total de alunos encontrados: {}", alunos.size());
        return alunos;
    }

    public Aluno incluirAluno(Aluno aluno) {
        logger.info("Incluindo aluno com CPF: {}", aluno.getCpf());

        if (alunoRepository.existsById(aluno.getCpf())) {
            logger.error("CPF já cadastrado: {}", aluno.getCpf());
            throw new IllegalArgumentException("CPF já cadastrado");
        }

        if (alunoRepository.isCpfRegisteredInDifferentTurma(aluno.getCpf(), aluno.getTurma())) {
            logger.error("Aluno com CPF {} já cadastrado em outra turma", aluno.getCpf());
            throw new IllegalArgumentException("Aluno já cadastrado em outra turma");
        }

        Aluno alunoSalvo = alunoRepository.save(aluno);
        logger.info("Aluno cadastrado com sucesso: {}", alunoSalvo);

        avaliarAlunos();

        return alunoSalvo;
    }

    public Aluno alterarAluno(String cpf, Aluno aluno) {
        logger.info("Alterando aluno com CPF: {}", cpf);

        Aluno alunoExistente = alunoRepository.findByCpf(cpf);
        if (alunoExistente == null) {
            logger.error("Aluno com CPF {} não encontrado", cpf);
            throw new IllegalArgumentException("Aluno com CPF " + cpf + " não encontrado");
        }

        alunoExistente.setNome(aluno.getNome());
        alunoExistente.setEndereco(aluno.getEndereco());
        alunoExistente.setTurma(aluno.getTurma());
        alunoExistente.setNota1(aluno.getNota1());
        alunoExistente.setNota2(aluno.getNota2());
        alunoExistente.setNota3(aluno.getNota3());

        Aluno alunoAlterado = alunoRepository.save(alunoExistente);
        logger.info("Aluno com CPF {} alterado com sucesso: {}", cpf, alunoAlterado);

        avaliarAlunos();

        return alunoAlterado;
    }

    public void excluirAluno(String cpf) {
        logger.info("Excluindo aluno com CPF: {}", cpf);

        if (!alunoRepository.existsById(cpf)) {
            logger.warn("Tentativa de exclusão de aluno inexistente com CPF: {}", cpf);
            throw new IllegalArgumentException("Aluno com CPF " + cpf + " não encontrado");
        }

        alunoRepository.deleteById(cpf);
        logger.info("Aluno com CPF {} excluído com sucesso", cpf);

        avaliarAlunos();
    }

    public void avaliarAlunos() {
        logger.info("Iniciando avaliação dos alunos");

        List<Aluno> alunos = alunoRepository.findAll();
        for (Aluno aluno : alunos) {
            double media = calcularMedia(aluno);
            aluno.setAprovado(media >= 7.0 ? "SIM" : "NÃO");
            alunoRepository.save(aluno);
            logger.info("Aluno {} avaliado: Média = {}, Aprovado = {}", aluno.getCpf(), media, aluno.getAprovado());
        }
    }

    private double calcularMedia(Aluno aluno) {
        logger.debug("Calculando média para o aluno com CPF: {}", aluno.getCpf());
        double media = (aluno.getNota1() + aluno.getNota2() + aluno.getNota3()) / 3.0;
        logger.debug("Média calculada para o aluno {}: {}", aluno.getCpf(), media);
        return media;
    }
}
