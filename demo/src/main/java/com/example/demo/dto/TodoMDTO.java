package com.example.demo.dto;

import com.example.demo.todo.Status;

public class TodoMDTO {
    private int id;
    private String title;
    private String content;
    private long duedate;
    private Status state;

    public TodoMDTO() {}

    public TodoMDTO(int id, String title, String content, long duedate, Status state) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.duedate = duedate;
        this.state = state;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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
        return "TodoMDTO{" +
               "id=" + id +
               ", title='" + title + '\'' +
               ", content='" + content + '\'' +
               ", duedate=" + duedate +
               ", state=" + state +
               '}';
    }
}
