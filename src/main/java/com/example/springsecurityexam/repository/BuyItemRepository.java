package com.example.springsecurityexam.repository;

import com.example.springsecurityexam.domain.BuyItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BuyItemRepository extends JpaRepository<BuyItem, Long> {
}
