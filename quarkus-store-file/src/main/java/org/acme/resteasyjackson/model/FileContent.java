package org.acme.resteasyjackson.model;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class FileContent implements PanacheRepository<FileContent> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public String fileName;

    @Lob
    public byte[] data;

    public LocalDateTime creationTime;

    @PrePersist
    public void prePersist() {
        creationTime = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }
}
