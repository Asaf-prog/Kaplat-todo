package com.example.demo.repository;

import com.example.demo.entity.TodoM;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface TodoMongo extends MongoRepository <TodoM,Integer> {

}
