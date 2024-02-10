package com.example.demo.repository;

import com.example.demo.entity.TodoP;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TodoPostgres extends JpaRepository<TodoP,Integer> {
    boolean existsByTitle(String title);
    Optional<TodoP> findTopByOrderByIdDesc();
    List<TodoP> findAll();
    void deleteById(int id);
}
