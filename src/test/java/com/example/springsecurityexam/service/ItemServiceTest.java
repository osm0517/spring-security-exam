package com.example.springsecurityexam.service;

import com.example.springsecurityexam.domain.Item;
import com.example.springsecurityexam.domain.Member;
import com.example.springsecurityexam.dto.item.ItemUpdateDto;
import com.example.springsecurityexam.repository.BuyItemRepository;
import com.example.springsecurityexam.repository.ItemRepository;
import com.example.springsecurityexam.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.event.annotation.BeforeTestClass;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ItemServiceTest {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    BuyItemRepository buyItemRepository;

    @Autowired
    MemberRepository memberRepository;

    private ItemService itemService;
    private Member member;

    @BeforeTestClass
    void clear(){
        itemRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
        buyItemRepository.deleteAllInBatch();
    }

    @BeforeEach
    void init(){
        itemService = new ItemService(itemRepository, buyItemRepository, memberRepository);
        member = new Member().createUserMember("test");
//testtesttest
    }

    @Nested
    @DisplayName("상품 등록")
    class addItem {

        @Test
        @DisplayName("성공 로직")
        void addItemSuccess() {
//            given
            memberRepository.save(member);

            Item testItem = new Item().createTestItem("test", member);

//            when
            Item addItem = itemService.addItem(testItem, member);

            Item findItem = itemRepository.findById(testItem.getId())
                    .orElseThrow(NoSuchElementException::new);

//            then
            assertThat(addItem).usingRecursiveComparison()
                    .isEqualTo(testItem);
            assertThat(findItem).usingRecursiveComparison()
                    .isEqualTo(testItem);
        }

    }

    @Nested
    @DisplayName("상품 수정")
    class editItem {

        @Test
        @DisplayName("성공 로직")
        void editItemSuccess() {
//            given
            memberRepository.save(member);

            Item testItem = new Item().createTestItem("test", member);
            String changeName = "changeItemName";

//            when
            itemRepository.save(testItem);

            ItemUpdateDto itemUpdateDto = new ItemUpdateDto(testItem.getId(), changeName, testItem.getPrice(), testItem.getQuantity());

            Item editItem = itemService.editItem(testItem.getId(), itemUpdateDto);

//            then
            assertThat(editItem).usingRecursiveComparison()
                    .isEqualTo(testItem);
            assertThat(editItem.getItemName()).isEqualTo(changeName);

        }

    }

    @Nested
    @DisplayName("단일 상품 보기")
    class findItem {

        @Test
        @DisplayName("성공 로직")
        void findItemSuccess() {
//            given
            memberRepository.save(member);

            Item testItem = new Item().createTestItem("test", member);

//            when
            itemRepository.save(testItem);

            Item findItem = itemService.findItem(testItem.getId());

//            then
            assertThat(findItem).usingRecursiveComparison()
                    .isEqualTo(testItem);

        }

    }

    @Nested
    @DisplayName("판매자들의 물건 확인")
    class findOtherItems {

        @Test
        @DisplayName("다른 판매자의 판매 목록 성공 로직")
        void findOtherItemsSuccess() {
//            given
            Member testUser = memberRepository.save(member);
            Member testA = memberRepository.save(new Member().createUserMember("testA"));

            Item itemA = new Item("itemA", 1000, 1000, testUser);
            Item itemB = new Item("itemB", 1000, 1000, testA);
            Item itemC = new Item("itemC", 1000, 1000, testA);
            Item itemD = new Item("itemD", 1000, 1000, testA);
            Item itemE = new Item("itemE", 1000, 1000, testA);

//            when
//            saveAll로 저장해도 됨
            itemRepository.save(itemA);
            itemRepository.save(itemB);
            itemRepository.save(itemC);
            itemRepository.save(itemD);
            itemRepository.save(itemE);

            List<Item> otherItems = itemService.findOtherItems(0, 3, testUser).getContent();

//            then
            assertThat(otherItems.size()).isEqualTo(3);
            assertThat(otherItems).contains(itemB, itemC, itemD);
            assertThat(otherItems).doesNotContain(itemA, itemE);

        }

        @Test
        @DisplayName("내가 올린 품목 확인 성공 로직")
        void findMyItemsSuccess() {
//            given
            Member testUser = memberRepository.save(member);
            Member testA = memberRepository.save(new Member().createUserMember("testA"));

            Item itemA = new Item("itemA", 1000, 1000, testUser);
            Item itemB = new Item("itemB", 1000, 1000, testA);

//            when
//            saveAll로 저장해도 됨
            itemRepository.save(itemA);
            itemRepository.save(itemB);

            List<Item> myItems = itemService.findMyItems(0, 3, testUser).getContent();

//            then
            assertThat(myItems.size()).isEqualTo(1);
            assertThat(myItems).contains(itemA);
            assertThat(myItems).doesNotContain(itemB);

        }

    }

    @Nested
    @DisplayName("내 상품을 삭제")
    class deleteItem {

        @Test
        @DisplayName("성공 로직")
        void deleteItemSuccess() {
//            given
            memberRepository.save(member);

            Item testItem = new Item().createTestItem("test", member);

//            when
            itemRepository.save(testItem);
//            controller 단에서 요청을 보내는 사람이 판매자가 맞는지를 확인함
            itemService.deleteItem(testItem.getId());

            Item findItem = itemRepository.findById(testItem.getId())
                    .orElse(null);

//            then
            assertThat(findItem).isNull();
        }

    }
}