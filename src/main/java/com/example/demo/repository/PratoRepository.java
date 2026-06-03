package com.example.demo.repository;

import com.example.demo.model.Prato;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PratoRepository extends JpaRepository<Prato, Long> {
}
