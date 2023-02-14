package com.example.springsecurityexam.repository;

import com.example.springsecurityexam.domain.Item;
import com.example.springsecurityexam.domain.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.event.annotation.AfterTestClass;
import org.springframework.test.context.event.annotation.BeforeTestClass;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest
@Transactional
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    MemberRepository memberRepository;

    long allCount;

    void clear(List<Item> items){
        for (Item item : items) {
            itemRepository.delete(item);
        }
    }

    void clear(Item item){
        itemRepository.delete(item);
    }

    @BeforeTestClass
    void beforeCount(){
        allCount = itemRepository.count();
    }


//    @AfterEach
//    void clear(){
//        repository.deleteAllInBatch();
//        memberRepository.deleteAllInBatch();
//    }

    @Test
    @DisplayName("생성 및 id로 찾기 테스트")
    void createTest(){
//        given
        Member testMember = new Member().createUserMember(UUID.randomUUID().toString());
        memberRepository.save(testMember);
        Member member = memberRepository.findById(testMember.getId()).get();
        Item testItem = new Item(UUID.randomUUID().toString(), 1000, 1000, member);

//        when
        itemRepository.save(testItem);

        Item findItem = itemRepository.findById(testItem.getId()).get();

//        then
        assertThat(findItem.getItemName()).isEqualTo(testItem.getItemName());
        clear(testItem);
        memberRepository.delete(member);
    }

    @Test
    @DisplayName("전체 찾기")
    void findAllTest(){
//        given
        Member testMember = new Member().createUserMember(UUID.randomUUID().toString());
        memberRepository.save(testMember);
        Member member = memberRepository.findById(testMember.getId()).get();
        Item itemA = new Item("itemA", 1000, 5, member);
        Item itemB = new Item("itemB", 1000, 5, member);
        Item itemC = new Item("itemC", 1000, 5, member);

//        when
        itemRepository.save(itemA);
        itemRepository.save(itemB);
        itemRepository.save(itemC);

        List<Item> items = itemRepository.findAll();

        System.out.println("allCount = " + allCount);

//        then
        assertThat(items.size()).isEqualTo(allCount+3);

        List<Item> testItems = new ArrayList<>();
        testItems.add(itemA);
        testItems.add(itemB);
        testItems.add(itemC);

        clear(testItems);
        memberRepository.delete(member);
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