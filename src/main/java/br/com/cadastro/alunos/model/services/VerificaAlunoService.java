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

    /**
     * Lista todos os alunos aprovados.
     * @return Lista de alunos aprovados.
     */
    public List<Aluno> listarAlunosAprovados() {
        List<Aluno> alunos = alunoRepository.findAll();
        List<Aluno> aprovados = new ArrayList<>();
        for (Aluno aluno : alunos) {
            if (calcularMedia(aluno) > 7.0) {
                aprovados.add(aluno);
                logger.info("Aluno aprovado: {}", aluno);
            }
        }
        return aprovados;
    }

    /**
     * Lista todos os alunos que foram reprovados so fizeram uma prova.
     * @return Lista de alunos reprovados.
     */
    public List<Aluno> listarAlunosReprovadosUmaProva() {
        List<Aluno> alunos = alunoRepository.findAll();
        List<Aluno> reprovadosUmaProva = new ArrayList<>();
        for (Aluno aluno : alunos) {
            if (aluno.getNota2() == 0 || aluno.getNota3() == 0) {
                reprovadosUmaProva.add(aluno);
                logger.info("Aluno reprovado em uma prova: {}", aluno);
            }
        }
        return reprovadosUmaProva;
    }


    /**
     * Busca os alunos aprovados em uma determinada turma.
     * @param turma O código da turma.
     * @param pageNumber O número da página.
     * @param pageSize O tamanho da página.
     * @return Página de alunos aprovados na turma especificada.
     */
    public Page<Aluno> buscarAlunosAprovadosPorTurma(String turma, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Aluno> alunosAprovados = alunoRepository.findByTurmaAndAprovado(turma, "SIM", pageable);
        logger.info("Alunos aprovados na turma {}: {}", turma, alunosAprovados.getContent());
        return alunosAprovados;
    }
    /**
     *
     * Calcula a média das notas de um aluno.
     * @param aluno Aluno para o qual a média será calculada.
     * @return A média das notas do aluno.
     */
    private double calcularMedia(Aluno aluno) {
        return (aluno.getNota1() + aluno.getNota2() + aluno.getNota3()) / 3.0;
    }
}
