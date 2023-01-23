//package com.example.springsecurityexam.controller;
//
//import com.example.springsecurityexam.domain.Item;
//import com.example.springsecurityexam.domain.ItemReport;
//import com.example.springsecurityexam.dto.ItemReportDto;
//import com.example.springsecurityexam.repository.ItemRepository;
//import com.example.springsecurityexam.repository.MemberRepository;
//import com.example.springsecurityexam.service.ReportService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
//import java.util.Optional;
//
//@Controller
//@Slf4j
//public class ReportController {
//
//    private ItemRepository itemRepository;
//
//    private MemberRepository memberRepository;
//
//    private ItemReportRepository itemReportRepository;
//
//    private UserReportRepository userReportRepository;
//
//    private ReportService reportService;
//
//    public ReportController(
//            ItemRepository itemRepository, MemberRepository memberRepository,
//            ItemReportRepository itemReportRepository, UserReportRepository userReportRepository,
//            ReportService reportService){
//        this.itemRepository = itemRepository;
//        this.memberRepository = memberRepository;
//        this.itemReportRepository = itemReportRepository;
//        this.userReportRepository = userReportRepository;
//        this.reportService = reportService;
//    }
//
//    @GetMapping("/items/report/{itemId}")
//    public String itemReportView(
//            @PathVariable int itemId,
//            ItemReportDto reportDto,
//            Model model
//    ){
//        Item item = itemRepository.findById(itemId);
//
//        model.addAttribute("item", item);
//        model.addAttribute("reportDto", reportDto);
//
//        return "/report/items/item";
//    }
//
//    @GetMapping("/items/report/{itemId}/result")
//    public String itemReportResultView(
//            @PathVariable int itemId,
//            Model model
//    ){
//        Optional<ItemReport> item = itemReportRepository.findById(itemId);
//
//        model.addAttribute("item", item.get());
//
//        return "/report/items/result";
//    }
//
//    @PostMapping("/items/report/{itemId}")
//    public String itemReport(
//            @PathVariable int itemId,
//            ItemReportDto reportDto,
//            RedirectAttributes redirectAttributes
//    ){
//        reportDto.setId(itemId);
//
//        redirectAttributes.addAttribute("itemId", itemId);
//
//        return "redirect:/items/report/{itemId}/result";
//    }
//}
