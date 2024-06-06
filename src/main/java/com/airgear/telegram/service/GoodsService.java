package com.airgear.telegram.service;

import com.airgear.model.*;
import com.airgear.telegram.dto.GoodsResponse;
import com.airgear.telegram.mapper.GoodsMapper;
import com.airgear.telegram.repository.GoodsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GoodsService {

    private final GoodsRepository goodsRepository;
    private final GoodsMapper goodsMapper;

    public GoodsResponse getGoodsById(Long id) {
        Optional<Goods> goodsOptional = goodsRepository.findById(id);
        return goodsOptional.map(goodsMapper::toGoodsResponseDTO).orElse(null);
    }
}
