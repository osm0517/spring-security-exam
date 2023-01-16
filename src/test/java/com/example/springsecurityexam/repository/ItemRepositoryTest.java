package com.example.springsecurityexam.repository;

import com.example.springsecurityexam.domain.Item;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest
class ItemRepositoryTest {

    @Autowired
    ItemRepository repository;

    @AfterEach
    void clear(){
        repository.deleteAllInBatch();
    }

    @Test
    @DisplayName("item을 저장")
    void save() {
//        given
        Item item = new Item("itemA", 1000, 2, 3);

//        when
        Item savedItem = repository.save(item);
        Item findItem = repository.findById(savedItem.getId());

//        then
        assertThat(savedItem).usingRecursiveComparison().isEqualTo(findItem);
    }

    @Test
    @DisplayName("판매자(본인)의 id로 items list를 찾음")
    void findByProducer() {
//        given
        Item item1 = new Item("item1", 1000, 2, 1);
        Item item2 = new Item("item2", 1000, 2, 1);
        Item item3 = new Item("item3", 1000, 2, 2);

//        when
        repository.save(item1);
        repository.save(item2);
        repository.save(item3);

        List<Item> items = repository.findByProducer(1);

//        then
        assertThat(items)
                .usingRecursiveFieldByFieldElementComparator()
                .size().isEqualTo(2);
        assertThat(items)
                .usingRecursiveFieldByFieldElementComparator()
                .contains(item1, item2);
    }

    @Test
    @DisplayName("수량 체크")
    void findQuantityById() {
//        given
        Item item1 = new Item("item1", 1000, 2, 1);
        Item item2 = new Item("item2", 1000, 1, 1);

//        when
        Item savedItem = repository.save(item1);
        repository.save(item2);

        int quantity = repository.findQuantityById(savedItem.getId());

//        then
        assertThat(quantity).isEqualTo(2);

    }

    @Test
    @DisplayName("모든 품목 목록을 찾음")
    void findAll() {
//        given
        Item item1 = new Item("item1", 1000, 2, 1);
        Item item2 = new Item("item2", 1000, 2, 1);
        Item item3 = new Item("item3", 1000, 2, 2);

//        when
        repository.save(item1);
        repository.save(item2);
        repository.save(item3);

        List<Item> items = repository.findAll();

//        then
        assertThat(items)
                .usingRecursiveFieldByFieldElementComparator()
                .size().isEqualTo(3);
        assertThat(items)
                .usingRecursiveFieldByFieldElementComparator()
                .contains(item1, item2, item3);
    }

    @Test
    void delete() {
        //        given
        Item item1 = new Item("item1", 1000, 2, 1);

//        when
        repository.save(item1);

        repository.deleteById(item1.getId());

        List<Item> items = repository.findAll();

//        then
        assertThat(items)
                .usingRecursiveFieldByFieldElementComparator()
                .size().isEqualTo(0);
    }
}