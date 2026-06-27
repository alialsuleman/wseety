package com.example.wseety.category;


import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Builder
@RequiredArgsConstructor
public class CategoryService {

    final private CategoryRepository categoryRepository ;

    public List<Category> getall ()
    {
       return  this.categoryRepository.findAll() ;
    }

}
