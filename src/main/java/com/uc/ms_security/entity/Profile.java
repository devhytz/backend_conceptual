package com.uc.ms_security.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "profiles")
@Data
@NoArgsConstructor
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20)
    private String phone;

    @Temporal(TemporalType.DATE)
    @Column(name = "birth_date")
    private LocalDate birthDate;
}
