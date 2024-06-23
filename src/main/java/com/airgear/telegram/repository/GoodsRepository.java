package com.airgear.telegram.repository;

import com.airgear.model.Goods;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GoodsRepository extends JpaRepository<Goods, Long> {
    List<Goods> findByNameContainingIgnoreCase(String keyword);

}
