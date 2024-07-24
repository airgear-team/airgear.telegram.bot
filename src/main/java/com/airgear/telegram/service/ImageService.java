package com.airgear.telegram.service;

import com.airgear.model.Goods;
import com.airgear.model.GoodsImages;
import com.airgear.telegram.repository.GoodsRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class ImageService {
    private final GoodsRepository goodsRepository;
    private static final Path BASE_DIR = Path.of("..", "images");

    @Transactional
    public Optional<byte[]> getFirstImageBytesByGoodsId(Long goodsId) throws IOException {
        Goods goods = goodsRepository.findById(goodsId)
                .orElseThrow(() -> new RuntimeException("Error goods ID " + goodsId));
        List<GoodsImages> images = goods.getImages();
        if (images.isEmpty()) {
            return Optional.empty();
        }
        String imagePath = BASE_DIR.resolve(images.get(0).getImageUrl()).toString();
        return Optional.of(Files.readAllBytes(Path.of(imagePath)));
    }
}
