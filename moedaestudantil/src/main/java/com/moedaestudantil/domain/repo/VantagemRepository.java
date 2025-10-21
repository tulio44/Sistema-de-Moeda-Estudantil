package com.moedaestudantil.domain.repo;

import com.moedaestudantil.domain.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VantagemRepository extends JpaRepository<Vantagem, Long> {
  Page<Vantagem> findByAtivoTrue(Pageable pageable);
}