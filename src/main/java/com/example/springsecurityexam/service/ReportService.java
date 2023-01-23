//package com.example.springsecurityexam.service;
//
//import com.example.springsecurityexam.domain.Item;
//import com.example.springsecurityexam.domain.ItemReport;
//import com.example.springsecurityexam.dto.ItemReportDto;
//import com.example.springsecurityexam.repository.ItemReportRepository;
//import com.example.springsecurityexam.repository.ItemRepository;
//import com.example.springsecurityexam.repository.MemberRepository;
//import com.example.springsecurityexam.repository.UserReportRepository;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//@Service
//@Slf4j
//public class ReportService {
//
//    private ItemRepository itemRepository;
//
//    private MemberRepository memberRepository;
//
//    private ItemReportRepository itemReportRepository;
//
//    private UserReportRepository userReportRepository;
//
//    public ReportService(
//            ItemRepository itemRepository, MemberRepository memberRepository,
//            ItemReportRepository itemReportRepository, UserReportRepository userReportRepository){
//        this.itemRepository = itemRepository;
//        this.memberRepository = memberRepository;
//        this.itemReportRepository = itemReportRepository;
//        this.userReportRepository = userReportRepository;
//    }
//
//    public ItemReport itemReport(ItemReportDto reportDto){
//        int itemId = reportDto.getId();
//
//        Item findItem = itemRepository.findById(itemId);
////        item을 찾고 없으면 예외 처리를 하기 위해서 사용
//        if(findItem == null){
//            return null;
//        }
//
//        ItemReport reportItem = itemReportRepository.findByItemId(itemId);
////        신고가 돼있지 않다면 다시 만들어야 함
//        if(reportItem == null){
////            ItemReport report = new ItemReport(itemId);
////            ItemReport savedItem = itemReportRepository.save(report);
//
////            if(savedItem == null){
////                return null;
////            }
////
////            return savedItem;
//            return null;
//        }
////        돼있다면 값만 추가해서 다시 저장
////        reportItem.changeNumber(reportItem.getNumberOfReport()+1);
////        ItemReport savedItem = itemReportRepository.save(reportItem);
//
////        if(savedItem == null){
////            return null;
////        }
//
//        return savedItem;
//    }
//}
