package br.com.cadastro.alunos.model.services;

import br.com.cadastro.alunos.model.entities.Aluno;
import br.com.cadastro.alunos.model.exceptions.ResourceNotFoundException;
import br.com.cadastro.alunos.model.exceptions.ServiceException;
import br.com.cadastro.alunos.model.exceptions.BusinessException;
import br.com.cadastro.alunos.model.repository.AlunoRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AlunoService {

    private final AlunoRepository alunoRepository;
    private static final Logger logger = LogManager.getLogger(AlunoService.class);

    @Autowired
    public AlunoService(AlunoRepository alunoRepository) {
        this.alunoRepository = alunoRepository;
    }

    public List<Aluno> listarAlunos() {
        if (logger.isInfoEnabled()) {
            logger.info("Listando todos os alunos");
        }
        try {
            List<Aluno> alunos = alunoRepository.findAll();
            if (logger.isInfoEnabled()) {
                logger.info("Total de alunos encontrados: {}", alunos.size());
            }
            return alunos;
        } catch (Exception e) {
            logger.error("Erro ao listar alunos", e);
            throw new ServiceException("Erro ao listar alunos", e);
        }
    }

    @Transactional
    public Aluno incluirAluno(Aluno aluno) {
        if (logger.isInfoEnabled()) {
            logger.info("Incluindo aluno com CPF: {}", aluno.getCpf());
        }

        try {
            // Validação do CPF
            if (aluno.getCpf() == null || aluno.getCpf().length() != 14) {
                logger.error("CPF inválido: {}", aluno.getCpf());
                throw new BusinessException("O CPF do aluno não é válido");
            }

            // Verifica se o CPF já está cadastrado
            if (alunoRepository.existsById(aluno.getCpf())) {
                logger.error("CPF já cadastrado: {}", aluno.getCpf());
                throw new BusinessException("CPF já cadastrado");
            }

            // Verifica se o CPF está cadastrado em outra turma
            if (alunoRepository.findById(aluno.getCpf()).isPresent()) {
                Aluno alunoExistente = alunoRepository.findById(aluno.getCpf()).get();
                if (!aluno.getTurma().equals(alunoExistente.getTurma())) {
                    logger.error("Aluno com CPF {} já cadastrado em outra turma", aluno.getCpf());
                    throw new BusinessException("Aluno já cadastrado em outra turma");
                }
            }

            // Calcula se o aluno está aprovado ou não
            double media = calcularMedia(aluno);
            aluno.setAprovado(media >= 7.0 ? "SIM" : "NÃO");

            // Salva o aluno no banco de dados
            Aluno alunoSalvo = alunoRepository.save(aluno);

            if (logger.isInfoEnabled()) {
                logger.info("Aluno cadastrado com sucesso: {}", alunoSalvo);
            }

            return alunoSalvo;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Erro ao incluir aluno", e);
            throw new ServiceException("Erro ao incluir aluno", e);
        }
    }

    @Transactional
    public Aluno alterarAluno(String cpf, Aluno aluno) {
        if (logger.isInfoEnabled()) {
            logger.info("Alterando aluno com CPF: {}", cpf);
        }

        try {
            // Verifica se o aluno existe
            Aluno alunoExistente = alunoRepository.findById(cpf)
                    .orElseThrow(() -> {
                        logger.error("Aluno com CPF {} não encontrado", cpf);
                        return new ResourceNotFoundException("Aluno com CPF " + cpf + " não encontrado");
                    });

            // Atualiza os dados do aluno
            alunoExistente.setNome(aluno.getNome());
            alunoExistente.setEndereco(aluno.getEndereco());
            alunoExistente.setTurma(aluno.getTurma());
            alunoExistente.setNota1(aluno.getNota1());
            alunoExistente.setNota2(aluno.getNota2());
            alunoExistente.setNota3(aluno.getNota3());

            // Recalcula se o aluno está aprovado
            double media = calcularMedia(alunoExistente);
            alunoExistente.setAprovado(media >= 7.0 ? "SIM" : "NÃO");

            // Salva as alterações
            Aluno alunoAlterado = alunoRepository.save(alunoExistente);

            if (logger.isInfoEnabled()) {
                logger.info("Aluno com CPF {} alterado com sucesso: {}", cpf, alunoAlterado);
            }

            return alunoAlterado;
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Erro ao alterar aluno", e);
            throw new ServiceException("Erro ao alterar aluno", e);
        }
    }

    @Transactional
    public void excluirAluno(String cpf) {
        if (logger.isInfoEnabled()) {
            logger.info("Excluindo aluno com CPF: {}", cpf);
        }

        try {
            // Verifica se o aluno existe
            if (!alunoRepository.existsById(cpf)) {
                logger.warn("Tentativa de exclusão de aluno inexistente com CPF: {}", cpf);
                throw new ResourceNotFoundException("Aluno com CPF " + cpf + " não encontrado");
            }

            // Exclui o aluno
            alunoRepository.deleteById(cpf);

            if (logger.isInfoEnabled()) {
                logger.info("Aluno com CPF {} excluído com sucesso", cpf);
            }
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Erro ao excluir aluno", e);
            throw new ServiceException("Erro ao excluir aluno", e);
        }
    }

    @Transactional
    public void avaliarAlunos() {
        if (logger.isInfoEnabled()) {
            logger.info("Iniciando avaliação dos alunos");
        }

        try {
            List<Aluno> alunos = alunoRepository.findAll();

            for (Aluno aluno : alunos) {
                double media = calcularMedia(aluno);
                String mediaFormatada = String.format("%.2f", media);
                aluno.setAprovado(media >= 7.0 ? "SIM" : "NÃO");
                alunoRepository.save(aluno);

                if (logger.isInfoEnabled()) {
                    logger.info("Aluno {} avaliado: Média = {}, Aprovado = {}",
                            aluno.getCpf(), mediaFormatada, aluno.getAprovado());
                }
            }
        } catch (Exception e) {
            logger.error("Erro ao avaliar alunos", e);
            throw new ServiceException("Erro ao avaliar alunos", e);
        }
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