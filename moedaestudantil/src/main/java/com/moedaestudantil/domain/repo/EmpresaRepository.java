package com.moedaestudantil.domain.repo;

import com.moedaestudantil.domain.model.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import com.moedaestudantil.domain.model.User;
import java.util.Optional;


public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
    Optional<Empresa> findByUser(User user);
}
