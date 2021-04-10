package org.acme.quartz.optaplaner;

public class Room {

    private String name;

    public Room() {
    }

    public Room(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

}
