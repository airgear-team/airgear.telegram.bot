package com.airgear.telegram.controller;

import com.airgear.telegram.dto.GoodsResponse;
import com.airgear.telegram.service.GoodsService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/goods")
@AllArgsConstructor
public class GoodsController {

    private final GoodsService goodsService;


    @GetMapping("/{goodsId}")
    public GoodsResponse getGoodsById(HttpServletRequest request,
                                      @AuthenticationPrincipal String email,
                                      @PathVariable Long goodsId) {
        return goodsService.getGoodsById(goodsId);
    }
    @GetMapping("/search")
    public ResponseEntity<List<GoodsResponse>> searchGoods(@RequestParam String keyword) {
        List<GoodsResponse> goodsResponses = goodsService.searchGoodsByKeyword(keyword);
        return new ResponseEntity<>(goodsResponses, HttpStatus.OK);
    }

}
