package com.example.springsecurityexam.domain;

import com.example.springsecurityexam.repository.ItemRepository;
import com.example.springsecurityexam.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ItemTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void clear(){
        itemRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("member와 item domain 연관 관계 테스트")
    void fkTest(){
//        given
        Member testUser = new Member().createUserMember("testUser");

        memberRepository.save(testUser);

        Item itemA = new Item("itemA", 1000, 5, testUser);

//        when
        Item savedItem = itemRepository.save(itemA);

//        then
        assertThat(testUser.getItems().size()).isEqualTo(1);
    }

}