package com.airgear.telegram.mapper;


import com.airgear.model.Location;
import com.airgear.telegram.dto.LocationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = RegionMapper.class)
public interface LocationMapper {

    @Mapping(source = "uniqueSettlementID", target = "locationId")
    @Mapping(source = "region.id", target = "regionId")
    LocationResponse toLocationResponse(Location location);

}
