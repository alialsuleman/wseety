package com.example.wseety.category;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "category")
@Getter
@Setter
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code; // ثابت مثل: ELECTRONICS

    @Column(nullable = false)
    private String nameAr;

    @Column(nullable = false)
    private String nameEn;

    public Category() {}

    public Category(String code, String nameAr, String nameEn) {
        this.code = code;
        this.nameAr = nameAr;
        this.nameEn = nameEn;
    }


}