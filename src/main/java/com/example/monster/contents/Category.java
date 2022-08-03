package com.example.monster.contents;



public enum Category {
    JAVA("java"), SPRING("spring"), DATABASE("database"), RESAPI("resapi");

    private final String name;

    Category (String name) {
        this.name = name;
    }

    public String getName() {
            return this.name;
    }
}
