package com.airgear.telegram.mapper;

import com.airgear.model.Goods;
import com.airgear.telegram.dto.GoodsRequest;
import com.airgear.telegram.dto.GoodsResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GoodsMapper {
    GoodsResponse toGoodsResponseDTO(Goods goods);

    @Mapping(target = "id", source = "id")
    Goods toGoods(GoodsRequest goodsRequest);
}
