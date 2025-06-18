package br.com.cadastro.alunos.model.repository;

import br.com.cadastro.alunos.model.entities.Aluno;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, String> {

    // Método existente
    Page<Aluno> findByTurmaAndAprovado(String turma, String aprovado, Pageable pageable);

    // Novo método
    Page<Aluno> findByTurma(String turma, Pageable pageable);

    // Consultas otimizadas sugeridas
    @Query("SELECT a FROM Aluno a WHERE (a.nota1 + a.nota2 + a.nota3) / 3.0 > 7.0")
    List<Aluno> findApproved();

    @Query("SELECT a FROM Aluno a WHERE (a.nota1 + a.nota2 + a.nota3) / 3.0 <= 7.0")
    List<Aluno> findFailed();

    @Query("SELECT a FROM Aluno a WHERE a.nota2 = 0 OR a.nota3 = 0")
    List<Aluno> findFailedInOneExam();

    @Query("SELECT a FROM Aluno a WHERE a.turma = :turma AND (a.nota1 + a.nota2 + a.nota3) / 3.0 > 7.0")
    Page<Aluno> findApprovedByClass(@Param("turma") String turma, Pageable pageable);

    @Query("SELECT a FROM Aluno a WHERE a.turma = :turma AND (a.nota1 + a.nota2 + a.nota3) / 3.0 <= 7.0")
    Page<Aluno> findFailedByClass(@Param("turma") String turma, Pageable pageable);

    @Query(value =
            "SELECT * FROM alunos WHERE " +
                    "((nota_1 <> 0.0 AND nota_2 = 0.0 AND nota_3 = 0.0) OR " +
                    "(nota_1 = 0.0 AND nota_2 <> 0.0 AND nota_3 = 0.0) OR " +
                    "(nota_1 = 0.0 AND nota_2 = 0.0 AND nota_3 <> 0.0))",
            nativeQuery = true)
    List<Aluno> findAlunosComApenasUmaProva();
}