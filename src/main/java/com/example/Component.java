package com.example;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class Component {

    private final String name;

    public Component(String name) {
        this.name = name;
    }

    public List<Callable<String>> getProcess() {
        List<Callable<String>> processes = new ArrayList<>();
        for (int i = 0; i < 1_000; i++) {
            int finalI = i;
            processes.add(() -> "Process from " + finalI + " : " + name);
        }

        return processes;
    }

}
