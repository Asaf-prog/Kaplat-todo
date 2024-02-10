package com.example.demo.service;

import com.example.demo.dto.TodoDTO;
import com.example.demo.entity.TodoM;
import com.example.demo.entity.TodoP;
import com.example.demo.todo.PersistenceMethod;
import com.example.demo.todo.Status;

import java.util.List;

public interface TodoService {
    boolean statusExistInDb(String title);
    void addNewTodo(TodoDTO todo);
    int  getNextId();
    int getSizeDB(PersistenceMethod persistenceMethod);
    int getSizeByStatus(PersistenceMethod persistenceMethod, Status status);
    List<TodoP> getAllTodoFromPostgres();
    List<TodoM> getAllTodoFromMongo();
    void deleteById(int id);
    String getIdBuyTitle(String title, int id);
    void saveTodoToPostgres(TodoP todoP);
    void saveTodoToMongo(TodoM todo);

}
