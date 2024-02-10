package com.example.demo.entity;

import com.example.demo.todo.Status;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "todos")
public class TodoM { // MongoDB entity
    @Id
    private String id;

    private String title;
    private String content;
    private long duedate;
    private Status state;

    public TodoM() {}

    public TodoM(String title, String content, long duedate, Status state) {
        this.title = title;
        this.content = content;
        this.duedate = duedate;
        this.state = state;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getDuedate() {
        return duedate;
    }

    public void setDuedate(long duedate) {
        this.duedate = duedate;
    }

    public Status getState() {
        return state;
    }

    public void setState(Status state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "TodoM{" +
               "id='" + id + '\'' +
               ", title='" + title + '\'' +
               ", content='" + content + '\'' +
               ", duedate=" + duedate +
               ", state=" + state +
               '}';
    }
}
