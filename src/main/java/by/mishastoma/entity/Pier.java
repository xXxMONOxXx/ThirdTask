package by.mishastoma.entity;

import by.mishastoma.util.PierIdGenerator;

public class Pier {

    private int id;

    public Pier(){
        id = PierIdGenerator.generate();
    }

    public int getId() {
        return id;
    }
}
