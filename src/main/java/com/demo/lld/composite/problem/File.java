package com.demo.lld.composite.problem;

public class File {
    private String name;

    public File(String name) {
        this.name = name;
    }

    public void ls(){
        System.out.println("File: " + name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
