package com.aviral.spring_boot_api.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
@Table(name = "months")
public class Month {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String monthName;

    private Double income = 0.0;
    private Double savingsPercent = 20.0;

    @OneToMany(mappedBy = "month", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<Category> categories;
}