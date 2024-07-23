package com.airgear.telegram.dto;

import com.airgear.model.GoodsCondition;
import com.airgear.model.GoodsStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;

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

    public String toFormattedString() {
        return "Назва: " + name + "\n" +
                "Опис: " + description + "\n" +
                "Ціна: " + price.getPriceAmount() + " " + price.getPriceCurrency() + "\n" +
                "Населений пункт: " + location.getSettlement() + "\n" +
                "Категорія: " + category.getName() + "\n" +
                "Телефон: " + phoneNumber + "\n" +
                "Стан: " + goodsCondition + "\n" +
                "Статус: " + status;
    }
}
