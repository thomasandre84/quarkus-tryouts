package org.example.ui.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@IdClass(FileActive.FileActiveId.class)
public class FileActive extends PanacheEntityBase {

    @Id
    @ManyToOne
    @JoinColumn(name = "version", referencedColumnName = "version")
    @JoinColumn(name = "template_name", referencedColumnName = "name")
    @JoinColumn(name = "template_category", referencedColumnName = "category")
    private FileVersion version;

    @Id
    @ManyToOne
    @JoinColumn(nullable = false, name = "category")
    private FileCategory category;

    private Integer active;

    @EqualsAndHashCode
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FileActiveId implements Serializable  {
        FileVersion version;
        FileCategory category;
    }
}
