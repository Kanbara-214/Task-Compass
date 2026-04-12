package com.example.demo.entity;

public enum TaskStatus {
    TODO("未着手", "status-todo"),
    IN_PROGRESS("進行中", "status-progress"),
    DONE("完了", "status-done");

    private final String label;
    private final String cssClass;

    TaskStatus(String label, String cssClass) {
        this.label = label;
        this.cssClass = cssClass;
    }

    public String getLabel() {
        return label;
    }

    public String getCssClass() {
        return cssClass;
    }
}
