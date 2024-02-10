package com.example.demo.repository;

import com.example.demo.entity.TodoP;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoPostgres extends JpaRepository<TodoP,Integer> {

}
