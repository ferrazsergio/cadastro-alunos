package br.com.cadastro.alunos.model.services;

import br.com.cadastro.alunos.model.entities.Aluno;
import br.com.cadastro.alunos.model.exceptions.ResourceNotFoundException;
import br.com.cadastro.alunos.model.exceptions.ServiceException;
import br.com.cadastro.alunos.model.repository.AlunoRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ConsultaAlunoService {

    private final AlunoRepository alunoRepository;
    private static final Logger logger = LogManager.getLogger(ConsultaAlunoService.class);

    @Autowired
    public ConsultaAlunoService(AlunoRepository alunoRepository) {
        this.alunoRepository = alunoRepository;
    }

    public List<Aluno> listarTodosAlunos() {
        if (logger.isInfoEnabled()) {
            logger.info("Listando todos os alunos");
        }
        try {
            return alunoRepository.findAll();
        } catch (Exception e) {
            logger.error("Erro ao listar todos os alunos", e);
            throw new ServiceException("Erro ao listar todos os alunos", e);
        }
    }

    public List<Aluno> listarAlunosAprovados() {
        if (logger.isInfoEnabled()) {
            logger.info("Listando alunos aprovados");
        }
        try {
            // Idealmente deveria ser uma query específica no repository
            List<Aluno> alunos = alunoRepository.findAll();
            List<Aluno> aprovados = new ArrayList<>();
            for (Aluno aluno : alunos) {
                if (calcularMedia(aluno) > 7.0) {
                    aprovados.add(aluno);
                }
            }
            return aprovados;
        } catch (Exception e) {
            logger.error("Erro ao listar alunos aprovados", e);
            throw new ServiceException("Erro ao listar alunos aprovados", e);
        }
    }

    public List<Aluno> listarAlunosReprovadosUmaProva() {
        if (logger.isInfoEnabled()) {
            logger.info("Listando alunos que fizeram apenas uma prova");
        }
        try {
            List<Aluno> alunos = alunoRepository.findAll();
            return alunos.stream()
                    .filter(aluno -> {
                        int provasFeitas = 0;

                        // Uma nota é considerada quando é maior que zero
                        if (aluno.getNota1() != null && aluno.getNota1() > 0) provasFeitas++;
                        if (aluno.getNota2() != null && aluno.getNota2() > 0) provasFeitas++;
                        if (aluno.getNota3() != null && aluno.getNota3() > 0) provasFeitas++;

                        return provasFeitas == 1;
                    })
                    .toList();
        } catch (Exception e) {
            logger.error("Erro ao listar alunos que fizeram apenas uma prova", e);
            throw new ServiceException("Erro ao listar alunos que fizeram apenas uma prova", e);
        }
    }

    public List<Aluno> listarTodosAlunosReprovados() {
        if (logger.isInfoEnabled()) {
            logger.info("Listando todos os alunos reprovados");
        }
        try {
            // Idealmente deveria ser uma query específica no repository
            List<Aluno> alunos = alunoRepository.findAll();
            List<Aluno> reprovados = new ArrayList<>();
            for (Aluno aluno : alunos) {
                if (calcularMedia(aluno) <= 7.0) {
                    reprovados.add(aluno);
                }
            }
            return reprovados;
        } catch (Exception e) {
            logger.error("Erro ao listar todos os alunos reprovados", e);
            throw new ServiceException("Erro ao listar todos os alunos reprovados", e);
        }
    }

    public Page<Aluno> buscarAlunosAprovadosPorTurma(String turma, int pageNumber, int pageSize) {
        if (logger.isInfoEnabled()) {
            logger.info("Buscando alunos aprovados na turma {} com paginação", turma);
        }
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<Aluno> alunosAprovados = alunoRepository.findByTurmaAndAprovado(turma, "SIM", pageable);
            if (alunosAprovados.isEmpty()) {
                throw new ResourceNotFoundException("Nenhum aluno aprovado encontrado na turma " + turma);
            }
            return alunosAprovados;
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Erro ao buscar alunos aprovados da turma {}", turma, e);
            throw new ServiceException("Erro ao buscar alunos aprovados por turma", e);
        }
    }

    public Page<Aluno> buscarAlunosReprovadosPorTurma(String turma, int pageNumber, int pageSize) {
        if (logger.isInfoEnabled()) {
            logger.info("Buscando alunos reprovados na turma {} com paginação", turma);
        }
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<Aluno> alunosReprovados = alunoRepository.findByTurmaAndAprovado(turma, "NÃO", pageable);
            if (alunosReprovados.isEmpty()) {
                throw new ResourceNotFoundException("Nenhum aluno reprovado encontrado na turma " + turma);
            }
            return alunosReprovados;
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Erro ao buscar alunos reprovados da turma {}", turma, e);
            throw new ServiceException("Erro ao buscar alunos reprovados por turma", e);
        }
    }

    public Page<Aluno> buscarTodosAlunosPorTurma(String turma, int pageNumber, int pageSize) {
        if (logger.isInfoEnabled()) {
            logger.info("Buscando todos os alunos na turma {} com paginação", turma);
        }
        try {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<Aluno> alunos = alunoRepository.findByTurma(turma, pageable);
            if (alunos.isEmpty()) {
                throw new ResourceNotFoundException("Nenhum aluno encontrado na turma " + turma);
            }
            return alunos;
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Erro ao buscar todos os alunos da turma {}", turma, e);
            throw new ServiceException("Erro ao buscar todos os alunos por turma", e);
        }
    }

    private double calcularMedia(Aluno aluno) {
        return (aluno.getNota1() + aluno.getNota2() + aluno.getNota3()) / 3.0;
    }
}