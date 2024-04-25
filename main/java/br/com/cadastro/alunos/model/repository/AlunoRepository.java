package br.com.cadastro.alunos.model.repository;

import br.com.cadastro.alunos.model.entities.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, String> {

    /**
     * Busca um aluno pelo CPF.
     * @param cpf O CPF do aluno a ser buscado.
     * @return O aluno encontrado ou null se não encontrado.
     */
    Aluno findByCpf(String cpf);

    /**
     * Verifica se o aluno já está cadastrado em uma turma diferente da especificada.
     * @param cpf O CPF do aluno a ser verificado.
     * @param turma A turma para a qual verificar a existência do aluno.
     * @return true se o aluno já estiver cadastrado em uma turma diferente, caso contrário false.
     */
    boolean existsByCpfAndTurmaNot(String cpf, String turma);


    /**
     * Busca os alunos de uma turma que foram aprovados.
     * @param turma O código da turma.
     * @param aprovado Indica se o aluno foi aprovado ("SIM" para aprovado, "NÃO" para reprovado).
     * @param pageable As informações de paginação.
     * @return Página de alunos aprovados na turma especificada.
     */
    Page<Aluno> findByTurmaAndAprovado(String turma, String aprovado, Pageable pageable);

}
