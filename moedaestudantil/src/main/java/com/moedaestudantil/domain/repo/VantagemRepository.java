package com.moedaestudantil.domain.repo;

import com.moedaestudantil.domain.model.Empresa;
import com.moedaestudantil.domain.model.Vantagem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VantagemRepository extends JpaRepository<Vantagem, Long> {

    List<Vantagem> findByEmpresa(Empresa empresa);

    List<Vantagem> findByAtivoTrue();
}
