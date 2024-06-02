package com.airgear.telegram.service;

import com.airgear.model.Goods;
import com.airgear.telegram.dto.GoodsResponse;
import com.airgear.telegram.mapper.GoodsMapper;
import com.airgear.telegram.repository.GoodsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GoodsService {

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private GoodsMapper goodsMapper;

    public GoodsResponse getGoodsById(Long goodsRequest) {
        Goods goods = goodsRepository.findById(goodsRequest)
                .orElseThrow(() -> new RuntimeException("Goods not found"));
        return goodsMapper.toGoodsResponseDTO(goods);
    }
}
