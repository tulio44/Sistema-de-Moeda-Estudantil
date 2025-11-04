package com.moedaestudantil.service;

import com.moedaestudantil.domain.model.Aluno;
import com.moedaestudantil.domain.repo.AlunoRepository;
import java.util.List;
import java.util.Optional;

public class AlunoService {
    private final AlunoRepository alunoRepository;

    public AlunoService(AlunoRepository alunoRepository) {
        this.alunoRepository = alunoRepository;
    }


    public Aluno salvar(Aluno aluno) {
        if (aluno == null) throw new IllegalArgumentException("Aluno não pode ser nulo");
        return alunoRepository.save(aluno);
    }


    public List<Aluno> listarTodos() {
        return alunoRepository.findAll();
    }


    public Optional<Aluno> buscarPorId(Long id) {
        if (id == null) throw new IllegalArgumentException("Id não pode ser nulo");
        return alunoRepository.findById(id);
    }


    public Aluno atualizar(Long id, Aluno alunoAtualizado) {
        if (id == null || alunoAtualizado == null) throw new IllegalArgumentException("Id e aluno não podem ser nulos");
        Optional<Aluno> alunoOpt = alunoRepository.findById(id);
        if (alunoOpt.isPresent()) {
            Aluno aluno = alunoOpt.get();
            aluno.setNome(alunoAtualizado.getNome());
            aluno.setCpf(alunoAtualizado.getCpf());
            aluno.setRg(alunoAtualizado.getRg());
            aluno.setEndereco(alunoAtualizado.getEndereco());
            aluno.setInstituicao(alunoAtualizado.getInstituicao());
            aluno.setCurso(alunoAtualizado.getCurso());
            aluno.setUser(alunoAtualizado.getUser()); // Atualiza o usuário vinculado (email/senha)
            return alunoRepository.save(aluno);
        }
        throw new RuntimeException("Aluno não encontrado");
    }

    public void deletar(Long id) {
        if (id == null) throw new IllegalArgumentException("Id não pode ser nulo");
        alunoRepository.deleteById(id);
    }
}
