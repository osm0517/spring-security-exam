package com.example.springsecurityexam.domain;

import com.example.springsecurityexam.repository.BuyItemRepository;
import com.example.springsecurityexam.repository.ItemRepository;
import com.example.springsecurityexam.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BuyItemTest {

    @Autowired
    private BuyItemRepository buyItemRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ItemRepository itemRepository;

    @BeforeEach
    void clear(){
        buyItemRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
        itemRepository.deleteAllInBatch();
    }

    @Test
    @Transactional
    @Rollback
    void fkTest(){
//        given
        Member member1 = new Member().createUserMember("testUserAAAA");
        Member member2 = new Member().createUserMember("testUserA");

        memberRepository.save(member1);
        memberRepository.save(member2);

        Item item1 = new Item("item1", 1000, 5, member1);
        Item item2 = new Item("item1", 1000, 5, member2);

        itemRepository.save(item1);
        itemRepository.save(item2);

        BuyItem buyItem = new BuyItem(member1, item1, 3);
//        when

        buyItemRepository.save(buyItem);

        BuyItem findItem = buyItemRepository.findById(buyItem.getId()).get();

//        then
        assertThat(findItem.getMember()).usingRecursiveComparison().isEqualTo(member1);
        assertThat(findItem.getItem()).usingRecursiveComparison().isEqualTo(item1);
        assertThat(member1.getBuyItems().size()).isEqualTo(1);
        assertThat(member1.getBuyItems()).usingRecursiveFieldByFieldElementComparator().contains(buyItem);

    }

}