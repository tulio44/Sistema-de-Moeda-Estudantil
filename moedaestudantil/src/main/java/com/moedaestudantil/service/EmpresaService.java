package com.moedaestudantil.service;

import com.moedaestudantil.domain.model.Empresa;
import com.moedaestudantil.domain.repo.EmpresaRepository;
import java.util.List;
import java.util.Optional;

public class EmpresaService {
    private final EmpresaRepository empresaRepository;

    public EmpresaService(EmpresaRepository empresaRepository) {
        this.empresaRepository = empresaRepository;
    }

    public Empresa salvar(Empresa empresa) {
        if (empresa == null) throw new IllegalArgumentException("Empresa não pode ser nula");
        return empresaRepository.save(empresa);
    }

    public List<Empresa> listarTodas() {
        return empresaRepository.findAll();
    }

    public Optional<Empresa> buscarPorId(Long id) {
        if (id == null) throw new IllegalArgumentException("Id não pode ser nulo");
        return empresaRepository.findById(id);
    }

    public Empresa atualizar(Long id, Empresa empresaAtualizada) {
        if (id == null || empresaAtualizada == null) throw new IllegalArgumentException("Id e empresa não podem ser nulos");
        Optional<Empresa> empresaOpt = empresaRepository.findById(id);
        if (empresaOpt.isPresent()) {
            Empresa empresa = empresaOpt.get();
            empresa.setNomeFantasia(empresaAtualizada.getNomeFantasia());
            empresa.setCnpj(empresaAtualizada.getCnpj());
            empresa.setEmailContato(empresaAtualizada.getEmailContato());
            empresa.setEndereco(empresaAtualizada.getEndereco());
            empresa.setUser(empresaAtualizada.getUser());
            // vantagens são gerenciadas separadamente
            return empresaRepository.save(empresa);
        }
        throw new RuntimeException("Empresa não encontrada");
    }

    public void deletar(Long id) {
        if (id == null) throw new IllegalArgumentException("Id não pode ser nulo");
        empresaRepository.deleteById(id);
    }
}
