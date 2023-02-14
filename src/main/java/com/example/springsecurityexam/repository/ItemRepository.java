package com.example.springsecurityexam.repository;

import com.example.springsecurityexam.domain.Item;
import com.example.springsecurityexam.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    long countByProducer(Member producer);

    long countByProducerNot(Member producer);

    Page<Item> findAllByProducer(Member producer, Pageable pageable);

    Page<Item> findAllByProducerNot(Member member, Pageable pageable);

    Optional<Item> findByItemName(String itemName);
}
