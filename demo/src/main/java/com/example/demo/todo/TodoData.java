package com.example.demo.todo;

public class TodoData {
    private int id;
    private String titleStat;
    private long date;
    private TodoStatus status;

    private String content;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitleStat() {
        return titleStat;
    }

    public long getDate() {
        return date;
    }

    public TodoStatus getStatus() {
        return status;
    }

    public void setStatus(TodoStatus status) {
        this.status = status;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public enum TodoStatus {
        PENDING, LATE, DONE
    }

}
