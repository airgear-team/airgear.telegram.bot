package com.airgear.telegram.dto;

import com.airgear.model.GoodsCondition;
import com.airgear.model.GoodsStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsResponse {
    private Long id;
    private String name;
    private String description;
    private PriceResponse price;
    @Valid
    private LocationResponse location;
    private CategoryResponse category;
    private String phoneNumber;
    private GoodsCondition goodsCondition;

    private GoodsStatus status;
}
