package com.Test.student.entity;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "student")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Size(min = 3)
    private String firstName;

    @Column(nullable = false)
    @Size(min = 3)
    private String lastName;

    @Column(nullable = false)
    @Past
    private LocalDate dob;

    @Column(nullable = false)
    @Pattern(regexp = "^[ABC]$")
    private String section;

    @Column(nullable = false)
    @Pattern(regexp = "^[MF]$")
    private String gender;

    private Integer marks1;

    private Integer marks2;

    private Integer marks3;

    private Integer total;

    private Float average;

    private String result;
}



