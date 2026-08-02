package com.demo.lld.composite.problem;

import java.util.List;

public class Folder {
    private String name;

    List<Object> children;

    public Folder(String name) {
        this.name = name;
    }

    public void ls(){
        
        for(Object child : children){
            if(child instanceof File){
                System.out.println("File: " + ((File) child).getName());
            } else if(child instanceof Folder){
                ((Folder) child).ls();
                System.out.println("Folder: " + ((Folder) child).getName());
            }
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Object> getChildren() {
        return children;
    }

    public void setChildren(List<Object> children) {
        this.children = children;
    }
    

}
