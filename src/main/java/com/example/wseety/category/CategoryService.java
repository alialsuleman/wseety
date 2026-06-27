package com.example.wseety.category;


import com.example.wseety.exceptionHandler.exception.BadRequestException;
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

    public Category findById (Long id )
    {
       return  this.categoryRepository.findById(id).orElseThrow(
                ()->    new BadRequestException("Selected Category is not Founded")
        ) ;

    }

    public Category save (Category category )
    {
        return  this.categoryRepository.save(category) ;

    }


}
