package com.example.educontrol.entry;

import java.time.LocalDateTime;
import java.util.UUID;

import com.example.educontrol.cafeteria.Cafeteria;
import com.example.educontrol.student.Student;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "entries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Entry {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false, foreignKey = @ForeignKey(name = "fk_entry_student"))
    private Student student;

    @ManyToOne
    @JoinColumn(name = "cafeteria_id", nullable = false, foreignKey = @ForeignKey(name = "fk_entry_cafeteria"))
    private Cafeteria cafeteria;

    @Column(nullable = false)
    private LocalDateTime entryTime;

    private LocalDateTime exitTime;

    @Column(nullable = false)
    private boolean completed;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public void markAsCompleted() {
        this.exitTime = LocalDateTime.now();
        this.completed = true;
        this.updatedAt = LocalDateTime.now();
    }
}
