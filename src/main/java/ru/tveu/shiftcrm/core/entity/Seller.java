package ru.tveu.shiftcrm.core.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "seller")
@Data
public class Seller {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "contact_info", nullable = false)
    private String contactInfo;

    @Column(name = "registration_date", nullable = false)
    @CreationTimestamp
    private LocalDateTime registrationDate;
}