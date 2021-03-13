package org.example.ui;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(FileVersion.FileVersionId.class)
public class FileVersion extends PanacheEntityBase {
    @Id
    private Long version;

    @Id
    private String name;

    @Id
    @ManyToOne
    @JoinColumn(nullable = false)
    private FileCategory category;

    private Instant created;

    private Instant activated;

    //@Lob // For LONGTEXT
    @Column(columnDefinition = "TEXT")
    private String content;



    @PrePersist
    public void created() {
        this.created = Instant.now();
    }

    @PreUpdate
    public void activated() {
        this.activated = Instant.now();
    }

    @EqualsAndHashCode
    static class FileVersionId implements Serializable {
        Long version;
        String name;
        FileCategory category;
    }

}