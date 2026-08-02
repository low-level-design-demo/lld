package com.demo.lld.composite.solutionFileSystem;

import java.util.List;

public class Folder implements FileSystem {
    private String name;
    private List<FileSystem> children;

    public Folder(String name) {
        this.name = name;
    }

    public void setChildren(List<FileSystem> children) {
        this.children = children;
    }

    @Override
    public void ls() {
        System.out.println("Folder: " + name);
        for (FileSystem child : children) {
            child.ls();
        }
    }

}
