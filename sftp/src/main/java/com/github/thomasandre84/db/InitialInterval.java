package com.github.thomasandre84.db;

import jakarta.persistence.*;

@Entity
@Table(name = "initial_interval")
public class InitialInterval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String description;


    protected InitialInterval() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
