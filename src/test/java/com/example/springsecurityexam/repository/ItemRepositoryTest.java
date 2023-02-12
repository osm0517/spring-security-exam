package com.example.springsecurityexam.repository;

import com.example.springsecurityexam.domain.Item;
import com.example.springsecurityexam.domain.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest
class ItemRepositoryTest {

    @Autowired
    ItemRepository repository;

    @Autowired
    MemberRepository memberRepository;

    @AfterEach
    void clear(){
        repository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("생성 및 id로 찾기 테스트")
    void createTest(){
//        given
        Member member = new Member().createUserMember("itemTestUser");

//        when
        memberRepository.save(member);

        Item itemA = new Item("itemA", 1000, 5, member);

        repository.save(itemA);
        Optional<Item> findItem = repository.findById(itemA.getId());

        Item result = findItem.get();

//        then
        assertThat(member.getItems().size()).isEqualTo(1);
        assertThat(result.getItemName()).isEqualTo("itemA");
    }

    @Test
    @DisplayName("전체 찾기")
    void findAllTest(){
//        given
        Member member = new Member().createUserMember("itemTestUser");

//        when
        memberRepository.save(member);

        Item itemA = new Item("itemA", 1000, 5, member);
        Item itemB = new Item("itemB", 1000, 5, member);
        Item itemC = new Item("itemC", 1000, 5, member);

        repository.save(itemA);
        repository.save(itemB);
        repository.save(itemC);

        List<Item> items = repository.findAll();

//        then
        assertThat(items.size()).isEqualTo(3);
    }

//    @Test
//    @DisplayName("id로 삭제")
//    void deleteByIdTest(){
////        given
//        Member member = new Member().createUserMember("itemTestUser");
//
////        when
//        memberRepository.save(member);
//
//        Item itemA = new Item("itemA", 1000, 5, member);
//        Item itemB = new Item("itemB", 1000, 5, member);
//
//        repository.save(itemA);
//        repository.save(itemB);
//
//        repository.deleteById(itemA.getId());
//
//        List<Item> items = repository.findAll();
//
////        then
//        assertThat(member.getItems().size()).isEqualTo(1);
//    }

}