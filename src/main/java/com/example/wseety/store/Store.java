package com.example.wseety.store;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name= "stores")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Store {

    public Store (UUID userId, String name , String description)
    {
        this.userId = userId ;
        this.name =  name ;
        this.description = description ;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;



    @NotBlank
    @NotNull
    private String name ;

    private String description;



    private UUID userId ;

    @Builder.Default
    private long numberOfProducts = 0  ;

    @Builder.Default
    private long numberOfCompletedOrders =0   ;

    @Builder.Default
    private long numberOfBrokers =0 ;

    @Builder.Default
    private boolean isAvailable  = true;

    @Builder.Default
    private long rate  = 0;


    @Column(name = "image_path")
    private String imagePath;

    public String getStoreId ()
    {
        return "STR-" + id.toString() ;
    }

}
