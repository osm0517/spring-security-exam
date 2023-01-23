package com.example.springsecurityexam.domain;

import com.example.springsecurityexam.repository.ItemRepository;
import com.example.springsecurityexam.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MemberTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void clear(){
        memberRepository.deleteAllInBatch();
        itemRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("fk 확인")
    void getItemsTest(){
//        given
        Member testUser1 = new Member().createUserMember("testUser1");
        Member testUser2 = new Member().createUserMember("testUser2");

//        when
        memberRepository.save(testUser1);
        memberRepository.save(testUser2);

        Item item1 = new Item("item1", 1000, 5, testUser1);
        Item item2 = new Item("item2", 2000, 6, testUser1);
        Item item3 = new Item("item3", 3000, 7, testUser2);
        Item item4 = new Item("item4", 4000, 8, testUser2);

        itemRepository.save(item1);
        itemRepository.save(item2);
        itemRepository.save(item3);
        itemRepository.save(item4);

        itemRepository.flush();

        item1.getProducer().getItems().remove(item1);
        itemRepository.deleteById(item1.getId());

        List<Item> items = testUser1.getItems();

        for (Item item : items) {
            System.out.println("item.getItemName() = " + item.getItemName());
        }

//        then
        assertThat(items.size()).isEqualTo(1);
    }

}