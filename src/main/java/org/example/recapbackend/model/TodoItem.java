package org.example.recapbackend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@With
@Data
public class TodoItem {
    @Id
    @GeneratedValue
    private Long id;
    private String description;
    private TodoStatus status;
}
