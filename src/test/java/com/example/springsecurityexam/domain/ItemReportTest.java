//package com.example.springsecurityexam.domain;
//
//import com.example.springsecurityexam.repository.ItemReportRepository;
//import com.example.springsecurityexam.repository.ItemRepository;
//import com.example.springsecurityexam.repository.MemberRepository;
//import com.example.springsecurityexam.repository.UserReportRepository;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import static org.assertj.core.api.Assertions.*;
//
//@SpringBootTest
//class ItemReportTest {
//
//    @Autowired
//    private ItemReportRepository itemReportRepository;
//    @Autowired
//    private UserReportRepository UserReportRepository;
//    @Autowired
//    private ItemRepository itemRepository;
//    @Autowired
//    private MemberRepository memberRepository;
//
//    @BeforeEach
//    void clear(){
//        itemReportRepository.deleteAllInBatch();
//        itemRepository.deleteAllInBatch();
//    }
//
//    @Test
//    void itemReportTest(){
////        given
//        Member producer = memberRepository.findById(52);
//        Item itemA = new Item("itemA", 1000, 5, producer);
//
////        when
//        Item savedItem = itemRepository.save(itemA);
//
//        ItemReport itemReport = new ItemReport(savedItem);
//
//        ItemReport reportedItem = itemReportRepository.save(itemReport);
//
////        then
//        assertThat(reportedItem.getItem().getId()).isEqualTo(savedItem.getId());
//        assertThat(producer.getItems())
//                .usingRecursiveFieldByFieldElementComparator()
//                .contains(itemA);
//    }
//}