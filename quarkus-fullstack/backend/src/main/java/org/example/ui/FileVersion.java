package org.example.ui;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@ToString
public class FileVersion extends PanacheEntityBase {

    @Id
    private Integer version;

    @Id
    private String name;

    @Id
    @ManyToOne
    @JoinColumn(nullable = false, name = "category")
    private FileCategory category;

    private Instant created;

    private Instant activated;

    //@Lob // For LONGTEXT
    @Column(columnDefinition = "TEXT")
    @JsonIgnore
    private String content;

    @PrePersist
    public void created() {
        this.created = Instant.now();
    }

    @EqualsAndHashCode
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    static class FileVersionId implements Serializable {
        Integer version;
        String name;
        FileCategory category;
    }

}
