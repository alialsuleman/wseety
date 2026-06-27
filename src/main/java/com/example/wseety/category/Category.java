package com.example.wseety.category;


import com.example.wseety.store.Store;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Store> stores = new ArrayList<>();

    public Category() {}

    public Category(String code, String nameAr, String nameEn) {
        this.code = code;
        this.nameAr = nameAr;
        this.nameEn = nameEn;
    }



    public void addStore(Store store) {
        stores.add(store);
        store.setCategory(this);
    }

    public void removeStore(Store store) {
        stores.remove(store);
        store.setCategory(null);
    }


    // equals/hashCode يدوياً، معتمد على id فقط
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Category)) return false;
        Category other = (Category) o;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }


}