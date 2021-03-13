package com.example.fitastic;

public class Workout {
    private long sets;
    private long reps;
    private String name;

    public Workout(long sets, long reps, String name) {
        this.sets = sets;
        this.reps = reps;
        this.name = name;
    }

    public long getSets() {
        return sets;
    }

    public void setSets(long sets) {
        this.sets = sets;
    }

    public long getReps() {
        return reps;
    }

    public void setReps(long reps) {
        this.reps = reps;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
