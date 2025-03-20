package br.com.cadastro.alunos.model.services;

import br.com.cadastro.alunos.model.entities.Aluno;
import br.com.cadastro.alunos.model.repository.AlunoRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Component
@Service
public class VerificaAlunoService {

    private final AlunoRepository alunoRepository;
    private static final Logger logger = LogManager.getLogger(VerificaAlunoService.class);

    @Autowired
    public VerificaAlunoService(AlunoRepository alunoRepository) {
        this.alunoRepository = alunoRepository;
    }

    public List<Aluno> listarAlunosAprovados() {
        if (logger.isInfoEnabled()) {
            logger.info("Listando alunos aprovados");
        }
        List<Aluno> alunos = alunoRepository.findAll();
        List<Aluno> aprovados = new ArrayList<>();
        for (Aluno aluno : alunos) {
            if (calcularMedia(aluno) > 7.0) {
                aprovados.add(aluno);
                if (logger.isInfoEnabled()) {
                    logger.info("Aluno aprovado: {}", aluno);
                }
            }
        }
        if (logger.isInfoEnabled()) {
            logger.info("Total de alunos aprovados: {}", aprovados.size());
        }
        return aprovados;
    }

    public List<Aluno> listarAlunosReprovadosUmaProva() {
        if (logger.isInfoEnabled()) {
            logger.info("Listando alunos reprovados em uma prova");
        }
        List<Aluno> alunos = alunoRepository.findAll();
        List<Aluno> reprovadosUmaProva = new ArrayList<>();
        for (Aluno aluno : alunos) {
            if (aluno.getNota2() == 0 || aluno.getNota3() == 0) {
                reprovadosUmaProva.add(aluno);
                if (logger.isInfoEnabled()) {
                    logger.info("Aluno reprovado em uma prova: {}", aluno);
                }
            }
        }
        if (logger.isInfoEnabled()) {
            logger.info("Total de alunos reprovados em uma prova: {}", reprovadosUmaProva.size());
        }
        return reprovadosUmaProva;
    }

    public List<Aluno> listarTodosAlunosReprovados() {
        if (logger.isInfoEnabled()) {
            logger.info("Listando todos os alunos reprovados");
        }
        List<Aluno> alunos = alunoRepository.findAll();
        List<Aluno> reprovados = new ArrayList<>();
        for (Aluno aluno : alunos) {
            if (calcularMedia(aluno) <= 7.0) {
                reprovados.add(aluno);
                if (logger.isInfoEnabled()) {
                    logger.info("Aluno reprovado: {}", aluno);
                }
            }
        }
        if (logger.isInfoEnabled()) {
            logger.info("Total de alunos reprovados: {}", reprovados.size());
        }
        return reprovados;
    }

    public Page<Aluno> buscarAlunosAprovadosPorTurma(String turma, int pageNumber, int pageSize) {
        if (logger.isInfoEnabled()) {
            logger.info("Buscando alunos aprovados na turma {} com paginação - página: {}, tamanho: {}", turma, pageNumber, pageSize);
        }
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Aluno> alunosAprovados = alunoRepository.findByTurmaAndAprovado(turma, "SIM", pageable);
        if (logger.isInfoEnabled()) {
            logger.info("Alunos aprovados encontrados na turma {}: {}", turma, alunosAprovados.getContent());
        }
        return alunosAprovados;
    }

    private double calcularMedia(Aluno aluno) {
        if (logger.isDebugEnabled()) {
            logger.debug("Calculando média para o aluno com CPF: {}", aluno.getCpf());
        }
        double media = (aluno.getNota1() + aluno.getNota2() + aluno.getNota3()) / 3.0;
        if (logger.isDebugEnabled()) {
            logger.debug("Média calculada para o aluno {}: {}", aluno.getCpf(), media);
        }
        return media;
    }
}