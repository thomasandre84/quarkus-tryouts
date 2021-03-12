package org.example.ui;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;
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
    private FileVersion version;

    @Id
    @ManyToOne
    private FileCategory category;

    private Long active;

    @EqualsAndHashCode
    static class FileActiveId implements Serializable  {
        FileVersion version;
        FileCategory category;
    }
}
