package com.airgear.telegram.mapper;

import com.airgear.model.Region;
import com.airgear.telegram.dto.RegionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RegionMapper {

    @Mapping(source = "id", target = "regionId")
    @Mapping(source = "region", target = "regionName")
    RegionResponse toDto(Region region);


}
