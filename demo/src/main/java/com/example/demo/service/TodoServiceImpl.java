package com.example.demo.service;

import com.example.demo.dto.TodoDTO;
import com.example.demo.dto.TodoMDTO;
import com.example.demo.entity.TodoM;
import com.example.demo.entity.TodoP;
import com.example.demo.repository.TodoMongo;
import com.example.demo.repository.TodoPostgres;
import com.example.demo.todo.PersistenceMethod;
import com.example.demo.todo.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    @Override
    public boolean statusExistInDb(String title) {
        boolean statusExistsInPostgres = todoPostgres.existsByTitle(title);
        boolean statusExistsInMongo = todoMongo.existsByTitle(title);
        return statusExistsInPostgres || statusExistsInMongo;
    }

    @Override
    public void addNewTodo(TodoDTO todoData) {
        TodoP todo = new TodoP();
        todo.setTitle(todoData.getTitle());
        todo.setContent(todoData.getContent());

        Date dueDate = todoData.getDueDate();
        long timestamp = dueDate.getTime();

        todo.setDuedate(timestamp);
        todo.setState(Status.PENDING);
        todo.setId(getNextId());
        TodoP savedTodoP = todoPostgres.save(todo);

        TodoM todoM = convertTodoPToTodoM(savedTodoP);
        todoMongo.save(todoM);
    }
    private TodoM convertTodoPToTodoM(TodoP todoP) {
        return new TodoM(todoP.getTitle(), todoP.getContent(), todoP.getDuedate(), todoP.getState());
    }
    @Override
    public int getNextId() {
        Integer highestId = todoPostgres.findTopByOrderByIdDesc().map(TodoP::getId).orElse(0);

        return highestId + 1;
    }

    @Override
    public int getSizeDB(PersistenceMethod persistenceMethod) {
        int counter = 0;
        if (persistenceMethod == PersistenceMethod.POSTGRES) {
            counter = todoPostgres.findAll().size();
        }
        else {
            counter = todoMongo.findAll().size();
        }
        return counter;
    }

    @Override
    public int getSizeByStatus(PersistenceMethod persistenceMethod, Status status) {
        int counter = 0;
        if (persistenceMethod == PersistenceMethod.POSTGRES) {
            switch (status) {
                case PENDING:
                    counter = todoPostgres.findAll().stream()
                            .filter(todo -> todo.getState() == Status.PENDING)
                            .collect(Collectors.toList())
                            .size();
                    break;
                case LATE:
                    counter = todoPostgres.findAll().stream()
                            .filter(todo -> todo.getState() == Status.LATE)
                            .collect(Collectors.toList())
                            .size();
                    break;
                case DONE:
                    counter = todoPostgres.findAll().stream()
                            .filter(todo -> todo.getState() == Status.DONE)
                            .collect(Collectors.toList())
                            .size();
                    break;
                default:
                    throw new IllegalArgumentException("Invalid status");
            }
        } else if (persistenceMethod == PersistenceMethod.MONGO) {
            switch (status) {
                case PENDING:
                    counter = todoMongo.findAll().stream()
                            .filter(todo -> todo.getState() == Status.PENDING)
                            .collect(Collectors.toList())
                            .size();
                    break;
                case LATE:
                    counter = todoMongo.findAll().stream()
                            .filter(todo -> todo.getState() == Status.LATE)
                            .collect(Collectors.toList())
                            .size();
                    break;
                case DONE:
                    counter = todoMongo.findAll().stream()
                            .filter(todo -> todo.getState() == Status.DONE)
                            .collect(Collectors.toList())
                            .size();
                    break;
                default:
                    throw new IllegalArgumentException("Invalid status");
            }
        } else {
            throw new IllegalArgumentException("Invalid persistence method");
        }
        return counter;
    }

    @Override
    public List<TodoP> getAllTodoFromPostgres() {
        return todoPostgres.findAll();
    }

    @Override
    public List<TodoM> getAllTodoFromMongo() {
        return todoMongo.findAll();
    }

    @Override
    public void deleteById(int id) {
        try {

            // Delete from MongoDB
            Optional<TodoP> pOptional = todoPostgres.findById(id);
            if (pOptional.isPresent()) {
                Optional<TodoM> todoM = todoMongo.findByTitle(pOptional.get().getTitle());
                if (todoM.isPresent()) {
                    String stringId = todoM.get().getId();
                    todoMongo.deleteById(stringId);
                }
            }

            // Delete from PostgreSQL
             todoPostgres.deleteById(id);

        } catch (Exception e) {
            System.err.println("Error deleting entity from MongoDB: " + e.getMessage());
        }

    }

    @Override
    public String getIdBuyTitle(String title, int id) {
        Optional<TodoP> pOptional = todoPostgres.findById(id);
        if (pOptional.isPresent()) {
            Optional<TodoM> todoM = todoMongo.findByTitle(pOptional.get().getTitle());
            if (todoM.isPresent()) {
                return todoM.get().getId();
            }
        }
        return null;
    }

    @Override
    public void saveTodoToPostgres(TodoP todoP) {
        todoPostgres.save(todoP);
    }

    @Override
    public void saveTodoToMongo(TodoM todo) {
        todoMongo.save(todo);
    }

    @Override
    public void changeIdToCorrectId(List<TodoM> todoMList) {
        for (TodoM todoM : todoMList) {
           Optional<TodoP> todoP = todoPostgres.findByTitle(todoM.getTitle());
           todoP.ifPresent(todoP1 -> todoM.setId(String.valueOf(todoP1.getId())));
        }
    }

    @Override
    public List<TodoMDTO> convertTodoMToDto(List<TodoM> listDataAfterFilter) {
        List<TodoMDTO> todoDTOList = new ArrayList<>();
        for (TodoM todoM : listDataAfterFilter) {
            TodoMDTO todoDTO = new TodoMDTO();
            todoDTO.setId(Integer.valueOf(todoM.getId()));
            todoDTO.setTitle(todoM.getTitle());
            todoDTO.setContent(todoM.getContent());
            todoDTO.setDuedate(todoM.getDuedate());
            todoDTO.setState(todoM.getState());
            todoDTOList.add(todoDTO);
        }
        return todoDTOList;
    }

}
