package com.example.demo.service;

import com.example.demo.repository.TodoMongo;
import com.example.demo.repository.TodoPostgres;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TodoServiceImpl implements TodoService {
    private TodoPostgres todoPostgres;
    private TodoMongo todoMongo;

    @Autowired
    public TodoServiceImpl (TodoPostgres todoPostgres, TodoMongo todoMongo) {
        this.todoPostgres = todoPostgres;
        this.todoMongo = todoMongo;
    }

    public void printAllData() {
        System.out.println("Data from PostgreSQL:");
        todoPostgres.findAll().forEach(System.out::println);

        System.out.println("Data from MongoDB:");
        todoMongo.findAll().forEach(System.out::println);
    }
}
