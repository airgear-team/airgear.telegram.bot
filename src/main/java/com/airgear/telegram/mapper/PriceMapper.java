package com.airgear.telegram.mapper;


import com.airgear.model.Price;
import com.airgear.telegram.dto.PriceResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PriceMapper {

    PriceResponse toDto(Price price);

}
