package com.demo.lld.composite.problem;

import java.util.List;

public class ProblemDemo {
    public static void main(String[] args) {
        File file1 = new File("file1.txt");
        File file2 = new File("file2.txt");
        File file3 = new File("file3.txt");

        Folder folder1 = new Folder("folder1");
        folder1.setChildren(List.of(file1, file2));

        Folder folder2 = new Folder("folder2");
        folder2.setChildren(List.of(file3, folder1));

        folder2.ls();
    }

}
