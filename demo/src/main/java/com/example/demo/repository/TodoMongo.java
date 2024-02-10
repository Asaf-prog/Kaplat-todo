package com.example.demo.repository;

import com.example.demo.entity.TodoM;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface TodoMongo extends MongoRepository <TodoM,Integer> {
    boolean existsByTitle(String title);
    List<TodoM> findAll();
    void deleteById(String id);
    Optional<TodoM> findById(String id);
    Optional<TodoM> findByTitle(String title);
}
