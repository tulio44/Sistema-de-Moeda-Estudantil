package com.moedaestudantil.domain.repo;

import com.moedaestudantil.domain.model.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {}