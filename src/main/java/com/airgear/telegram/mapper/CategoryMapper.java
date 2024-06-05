package com.airgear.telegram.mapper;

import com.airgear.model.Category;
import com.airgear.telegram.dto.CategoryResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryResponse toDto(Category category);

}
