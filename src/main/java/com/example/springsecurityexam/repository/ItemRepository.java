package com.example.springsecurityexam.repository;

import com.example.springsecurityexam.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {

//    producer
    Item save(Item item);
    List<Item> findByProducer(int producer);

//    consumer

    /**
     * 수량을 파라미터로 넘겨주면 구매를 진행
     * @param quantity
     * @return
     */
    Item save(int quantity);
    @Query(
            nativeQuery = true,
            value = "SELECT quantity FROM item WHERE id = :id"
    )
    int findQuantityById(@Param("id") int id);

//    manager


//    common
    Item findById(int id);
    List<Item> findAll();
    void deleteById(int id);

}
