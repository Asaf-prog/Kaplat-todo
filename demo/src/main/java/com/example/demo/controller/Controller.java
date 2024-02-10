package com.example.demo.controller;

import com.example.demo.dto.TodoDTO;
import com.example.demo.entity.TodoM;
import com.example.demo.entity.TodoP;
import com.example.demo.error.ErrorMessage;
import com.example.demo.service.TodoService;
import com.example.demo.todo.*;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.apache.logging.log4j.Level;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.HttpStatus;
import java.util.*;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import org.springframework.http.ResponseEntity;
import java.time.LocalDateTime;
import java.time.ZoneId;

@RestController
@RequestMapping("/todo")
public class Controller {

    private TodoService todoService;

    @Autowired
    public Controller (TodoService todoService) {
        this.todoService =todoService;
    }

    public static Integer counter = 1;
    private TodoRep todoDataList = new TodoRep();
    public static final Logger loggerTodo = LogManager.getLogger("todo-logger");
    public static final Logger loggerReq = LogManager.getLogger("request-logger");

    private static void addRequestDuration(long start) {
        long durTime = System.currentTimeMillis() - start;
        loggerReq.debug("request #{} duration: {}ms", counter, durTime);
    }

    private static void addRequest() {
        ThreadContext.put("requestNumber", counter.toString());
        HttpServletRequest requestData = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String requestMethod = requestData.getMethod();
        String resourceData = requestData.getRequestURI();
        loggerReq.info("Incoming request | #{} | resource: {} | HTTP Verb {}", counter, resourceData, requestMethod);
    }

    @RestController
    @RequestMapping("/")
    public class LogsController {

        @RequestMapping(value = "/logs/level", method = RequestMethod.GET)
        public String getLoggerLevel(@RequestParam (name = "name") String loggerName) {

            if (loggerName.equals("request-logger")) {
                addRequest();

                long start = System.currentTimeMillis();
                long durData = System.currentTimeMillis() - start;
                addRequestDuration(start);

                counter++;

                return loggerReq.getLevel().name().toUpperCase();
            }
            if (!loggerName.equals("todo-logger")) {
                addRequest();
                long start = System.currentTimeMillis();
                long durTime = System.currentTimeMillis() - start;

                addRequestDuration(start);

                counter++;
                return loggerTodo.getLevel().name().toUpperCase();
            } else {
                return "Invalid logger name";
            }
        }

        @RequestMapping(value = "/logs/level", method = RequestMethod.PUT)
        public String setLoggerLevelData(@RequestParam (name = "name") String name, @RequestParam(name = "loggerLevelD") String loggerLevelD) {

            if (!loggerLevelD.equals("ERROR") && !loggerLevelD.equals("INFO") && !loggerLevelD.equals("DEBUG")) {
                return "Invalid logger level";
            }

            if (!name.equals("request-logger") && !name.equals("todo-logger")) {
                return "Invalid logger name";
            }

            LoggerContext loggerContext = (LoggerContext) LogManager.getContext(false);
            Configuration configuration = loggerContext.getConfiguration();
            org.apache.logging.log4j.core.config.LoggerConfig loggerConfig = configuration.getLoggerConfig(name);
            Level level = Level.valueOf(loggerLevelD.toUpperCase());
            loggerConfig.setLevel(level);
            loggerContext.updateLoggers();

            addRequest();

            long start = System.currentTimeMillis();
            addRequestDuration(start);
            counter++;

            return level.name();
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthC() {
        addRequest();

        long start = System.currentTimeMillis();
        addRequestDuration(start);

        return ResponseEntity.ok().body("OK");
    }

    @PostMapping("")
    @ResponseBody
    public ResponseEntity<?> createNewTodo(@RequestBody TodoDTO todo) {

        if (todoService.statusExistInDb(todo.getTitle())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorMessage("Error: TODO with the title [" + todo.getTitle() + "] already exists in the system"));
        }
        LocalDateTime localDateTime = LocalDateTime.now();

        Date dueDate = todo.getDueDate();
        long timestamp =dueDate.getTime();

        Instant instant = Instant.ofEpochMilli(timestamp);
        LocalDateTime newTimeByCal = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

        if (newTimeByCal.isBefore(localDateTime)) {

            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorMessage("Error: Can’t create new TODO that its due date is in the past"));
        }
        addRequest();
        long start = System.currentTimeMillis();

        todoService.addNewTodo(todo);

        addRequestDuration(start);
        loggerTodo.info("Creating new TODO with Title [{}]", todo.getTitle());

        return ResponseEntity.ok(new resultMessage(todoService.getNextId() - 1));
    }

    @GetMapping("/size")
    public ResponseEntity<?> sizeWithDFilter(@RequestParam (name = "status") String status,
                                             @RequestParam(name = "persistenceMethod")PersistenceMethod persistenceMethod) {

        if(Arrays.stream(TodoData.TodoStatus.values()).noneMatch(tds -> tds.toString().equals(status)) && !status.equals("ALL")) {
            return ResponseEntity.badRequest().body("Invalid request");
        }

        if (persistenceMethod != PersistenceMethod.POSTGRES && persistenceMethod != PersistenceMethod.MONGO) {
            return ResponseEntity.badRequest().body("Invalid persistence method");
        }

        long start = System.currentTimeMillis();
        addRequest();

        Integer integerCounter;

        if (status.equals("ALL")) {
            integerCounter = todoService.getSizeDB(persistenceMethod);
        }
        else {
            Status stat = Status.valueOf(status);
            integerCounter = todoService.getSizeByStatus(persistenceMethod,stat);
        }
        loggerTodo.info("Total TODOs count for state {} is {}", status,integerCounter);

        addRequestDuration(start);

        return ResponseEntity.ok().body(new resultMessage(integerCounter));
    }

    @GetMapping("/content")
    public ResponseEntity<?> getTodoData(@RequestParam (name = "status") String status,
                                         @RequestParam(name = "howToSort", required = false) String howToSort,
                                         @RequestParam(name = "persistenceMethod")PersistenceMethod persistenceMethod) {

        if(Arrays.stream(TodoData.TodoStatus.values()).noneMatch(tds -> tds.toString().equals(status)) && !status.equals("ALL"))
            return ResponseEntity.badRequest().body("Invalid request");

        if(howToSort != null)
            if(!howToSort.equals("ID") && !howToSort.equals("DUE_DATE") && !howToSort.equals("TITLE"))
                return ResponseEntity.badRequest().body("Invalid request");

        addRequest();
        long start = System.currentTimeMillis();

        if (persistenceMethod == PersistenceMethod.POSTGRES) {
            List<TodoP> listDataAfterFilter;

            if(status.equals("ALL")) {
                listDataAfterFilter = todoService.getAllTodoFromPostgres();
            }
            else {
                 listDataAfterFilter = todoService.getAllTodoFromPostgres()
                        .stream()
                        .filter(td -> status.equals(td.getState().toString()))
                        .toList();
            }
            if(howToSort != null) {
                if (howToSort.equals("ID")) {
                    listDataAfterFilter = listDataAfterFilter
                            .stream()
                            .sorted(Comparator.comparing(TodoP::getId))
                            .toList();
                } else if (howToSort.equals("DUE_DATE")) {
                    listDataAfterFilter = listDataAfterFilter
                            .stream()
                            .sorted(Comparator.comparing(TodoP::getDuedate))
                            .toList();
                } else {
                    listDataAfterFilter = listDataAfterFilter
                            .stream()
                            .sorted(Comparator.comparing(TodoP::getTitle, String.CASE_INSENSITIVE_ORDER))
                            .toList();
                }
            }
            else {
                howToSort = "ID";
            }

            addRequestDuration(start);
            loggerTodo.info("Extracting todos content. Filter: {} | Sorting by: {}", status, howToSort);

            return ResponseEntity.ok().body(new resultMessage(listDataAfterFilter));
        }
        else {
            List<TodoM> listDataAfterFilter;

            if(status.equals("ALL")) {
                listDataAfterFilter = todoService.getAllTodoFromMongo();
            }
            else {
                listDataAfterFilter = todoService.getAllTodoFromMongo()
                        .stream()
                        .filter(td -> status.equals(td.getState().toString()))
                        .toList();
            }
            if(howToSort != null) {
                if (howToSort.equals("ID")) {
                    listDataAfterFilter = listDataAfterFilter
                            .stream()
                            .sorted(Comparator.comparing(TodoM::getId))
                            .toList();
                } else if (howToSort.equals("DUE_DATE")) {
                    listDataAfterFilter = listDataAfterFilter
                            .stream()
                            .sorted(Comparator.comparing(TodoM::getDuedate))
                            .toList();
                } else {
                    listDataAfterFilter = listDataAfterFilter
                            .stream()
                            .sorted(Comparator.comparing(TodoM::getTitle, String.CASE_INSENSITIVE_ORDER))
                            .toList();
                }
            }
            else {
                howToSort = "ID";
            }

            addRequestDuration(start);
            loggerTodo.info("Extracting todos content. Filter: {} | Sorting by: {}", status, howToSort);

            return ResponseEntity.ok().body(new resultMessage(listDataAfterFilter));
        }
    }

    @PutMapping("")
    public ResponseEntity<?> updateStatus(@RequestParam (name = "id") int id, @RequestParam (name = "status") String status) {

        long startTime = System.currentTimeMillis();
        addRequest();
        List<TodoM> todoM = todoService.getAllTodoFromMongo();
        List<TodoP> todoP = todoService.getAllTodoFromPostgres();

        if (todoP.stream().noneMatch(td -> td.getId() == id)) {
            addRequestDuration(startTime);
            ThreadContext.put("requestNumber", counter.toString());

            loggerTodo.info("Update TODO Id [{}] state to {}", id, status);

            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorMessage("Error: their is no TODO with the  Id: " + id));
        }


        TodoP updateTodo = todoP
                .stream()
                .filter(td -> td.getId() == id)
                .findFirst()
                .get()
                ;

        String idByTitle = todoService.getIdBuyTitle(updateTodo.getTitle(), id);
        TodoM updateTodoM = todoM
                .stream()
                .filter(td -> td.getId().equals(idByTitle))
                .findFirst()
                .get()
                ;
        String nameP = updateTodo.getState().name();

        if (status.equals(TodoData.TodoStatus.DONE.name())) {
            updateTodo.setState(Status.DONE);
        } else if (status.equals(TodoData.TodoStatus.PENDING.name())) {
            updateTodo.setState(Status.PENDING);
        } else if (status.equals(TodoData.TodoStatus.LATE.name())) {
            updateTodo.setState(Status.LATE);
        } else {
            addRequestDuration(startTime);
            loggerTodo.info("Update TODO Id [{}] state to {}", id, status);
            loggerTodo.debug("Todo Id [{}] state change: {} -> {}", id, nameP, status);

            return ResponseEntity.ok().body(new resultMessage(nameP));
        }
        todoService.saveTodoToPostgres(updateTodo);
        todoService.saveTodoToMongo(updateTodoM);


        return ResponseEntity.ok().body(new resultMessage(nameP));
    }

    @DeleteMapping ("")
    public ResponseEntity<?> deleteByID(@RequestParam (name = "id") int id) {

        if(todoService.getAllTodoFromPostgres().stream().noneMatch(td -> td.getId() == id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorMessage("Error: no such TODO with id " + id));
        }

        long start = System.currentTimeMillis();
        addRequest();

        todoService.deleteById(id);

        loggerTodo.info("Removing Todo id {}", id);
        addRequestDuration(start);

        return ResponseEntity.ok().body(new resultMessage(todoService.getAllTodoFromPostgres().size()));
    }
}
