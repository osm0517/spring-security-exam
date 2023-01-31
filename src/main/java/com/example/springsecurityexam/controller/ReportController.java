package com.example.springsecurityexam.controller;

import com.example.springsecurityexam.domain.Item;
import com.example.springsecurityexam.dto.ReportDto;
import com.example.springsecurityexam.repository.ItemRepository;
import com.example.springsecurityexam.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ReportController {

    private final ItemRepository itemRepository;

    private final MemberRepository memberRepository;

    @PostMapping("/items/report/{itemId}")
    public String itemReportView(
            @PathVariable long itemId,
            @Validated @ModelAttribute(name = "report") ReportDto dto,
            BindingResult bindingResult,
            Model model
    ){
        if(bindingResult.hasErrors()){

        }

        Optional<Item> item = itemRepository.findById(itemId);

        if(item.isEmpty()){

        }

        model.addAttribute("item", item);
//        model.addAttribute("reportDto", reportDto);

        return "/report/items/item";
    }

    @PostMapping("/member/report/{userId}")
    public String itemReportResultView(
            @PathVariable int itemId,
            Model model
    ){
//        Optional<ItemReport> item = itemReportRepository.findById(itemId);

//        model.addAttribute("item", item.get());

        return "/report/items/result";
    }
}
