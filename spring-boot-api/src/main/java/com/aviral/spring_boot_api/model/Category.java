package com.aviral.spring_boot_api.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Double budget;

    @ManyToOne
    @JoinColumn(name = "month_id")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Month month;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<Transaction> transactions;
}