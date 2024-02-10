package com.example.demo.error;

import com.example.demo.controller.Controller;
import org.apache.logging.log4j.ThreadContext;

import static com.example.demo.controller.Controller.loggerTodo;

public class ErrorMessage {
        private String errorMessage;

        public ErrorMessage(String message) {

            ThreadContext.put("requestNumber", Controller.counter.toString());
            loggerTodo.error(message);

            this.errorMessage = message;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }


