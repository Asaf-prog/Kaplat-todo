package com.example.demo.entity;

import com.example.demo.todo.Status;
import jakarta.persistence.*;

@Entity
@Table(name = "todos")
public class TodoP { // Postgres entity

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rawid")
    private int id;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @Column(name = "duedate")
    private long duedate;

    @Column(name = "state")
    private Status state;

    public TodoP()  {}

    public TodoP(String title, String content, long duedate, Status state) {
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
        return "Todo{" +
               "id=" + id +
               ", title='" + title + '\'' +
               ", content='" + content + '\'' +
               ", duedate=" + duedate +
               ", state=" + state +
               '}';
    }
}
