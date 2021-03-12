package org.example.ui;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import java.time.Instant;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class FileCategory extends PanacheEntityBase {
    @Id
    private String category;

    private Instant created;

    @PrePersist
    public void setDate(){
        this.created = Instant.now();
    }
}
