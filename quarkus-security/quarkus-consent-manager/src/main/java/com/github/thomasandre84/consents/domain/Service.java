package com.github.thomasandre84.consents.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
public class Service {

    @Id
    private Long id;

    private String name;
}
