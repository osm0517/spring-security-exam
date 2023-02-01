package com.example.springsecurityexam.controller;

import com.example.springsecurityexam.config.utils.SessionUtils;
import com.example.springsecurityexam.domain.Item;
import com.example.springsecurityexam.domain.Member;
import com.example.springsecurityexam.dto.item.ItemSaveDto;
import com.example.springsecurityexam.dto.item.ItemBuyDto;
import com.example.springsecurityexam.dto.item.ItemUpdateDto;
import com.example.springsecurityexam.service.ItemService;
import com.example.springsecurityexam.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@Slf4j
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final MemberService memberService;

    private int size = 5;

    @GetMapping
    public String itemsView(
            @RequestParam int page,
            Model model
    ){
        Page<Item> items = itemService.findItems(page-1, size);
        int paging = itemService.paginationAll(size);

        List<Integer> range = IntStream.rangeClosed(1, paging)
                .boxed()
                .collect(Collectors.toList());

        model.addAttribute("items", items);
        model.addAttribute("paging", range);

        return "/items/items";
    }

    @GetMapping("/add")
    public String addItemView(
            Model model
    ){

        model.addAttribute("item", new ItemSaveDto());

        return "/items/addForm";
    }

    @GetMapping("/{itemId}")
    public String itemView(
            @PathVariable int itemId,
            Model model
    ){

        Item item = itemService.findItem(itemId);
        model.addAttribute("item", item);
        model.addAttribute("producer", item.getProducer());

        return "/items/item";
    }

    @GetMapping("/{itemId}/producer/info")
    public String producerInfoForm(
            @PathVariable int itemId,
            Model model
    ){

        Item item = itemService.findItem(itemId);

        model.addAttribute("itemId", itemId);
        model.addAttribute("producer", item.getProducer());

        return "/member/producer/info";
    }

    @GetMapping("/{itemId}/producer/info/items")
    public String producerItemsForm(
            @PathVariable int itemId,
            Model model
    ){

        Item item = itemService.findItem(itemId);

        model.addAttribute("producer", item.getProducer());

        return "/member/producer/items";
    }

    @GetMapping("/{itemId}/buy/popup")
    public String buyForm(
            @PathVariable int itemId,
            Model model
    ){
        Item item = itemService.findItem(itemId);

        model.addAttribute("item", item);
        model.addAttribute("dto", new ItemBuyDto());

        return "/items/buyForm";
    }

    @PostMapping("/{itemId}/buy/popup")
    public void buyItem(
            @PathVariable long itemId,
            @ModelAttribute ItemBuyDto dto,
            @SessionAttribute(name = SessionUtils.session_login_id) long userId
    ){
        log.debug("buy debug");
        try {
            itemService.buyItem(itemId, userId, dto);
        }catch (NoSuchElementException e) {
            log.debug("db에 해당하는 id가 없음");
        }catch (Exception e){
            log.debug(e.getMessage());
        }
    }

    @GetMapping("/edit/{itemId}")
    public String editView(
            @PathVariable int itemId,
            Model model
    ){
        Item findItem = itemService.findItem(itemId);

        model.addAttribute("item", new ItemUpdateDto(findItem.getId(), findItem.getItemName(), findItem.getPrice(), findItem.getQuantity()));

        return "/items/editForm";
    }

    @PostMapping("/edit/{itemId}")
    public String editItem(
            @PathVariable int itemId,
            @Validated @ModelAttribute(name = "item") ItemUpdateDto updateItem,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model

    ){
        if(bindingResult.hasErrors()){
            log.debug("error = {}", bindingResult);
            return "/items/editForm";
        }

        Item updatedItem = itemService.editItem(itemId, updateItem);

        model.addAttribute("item", updatedItem);

        redirectAttributes.addAttribute("itemId", updatedItem.getId());

        return "redirect:/items/{itemId}";
    }

    @PostMapping("/delete/{itemId}")
    public String deleteItem(
            @PathVariable int itemId,
            RedirectAttributes redirectAttributes,
            @SessionAttribute(name = SessionUtils.session_login_id) long userId

    ){
        itemService.deleteItem(itemId);

        redirectAttributes.addAttribute("userId", userId);

        return "redirect:/{userId}/items";
    }

    @PostMapping("/add")
    public String addItem(
            @Validated @ModelAttribute(name = "item") ItemSaveDto dto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            @SessionAttribute(name = SessionUtils.session_login_id) long userId
    ){

        if(bindingResult.hasErrors()){
            log.debug("error = {}", bindingResult);
            return "/items/addForm";
        }

        Member member = memberService.checkSession(userId);

        Item item = new Item(dto.getItemName(), dto.getPrice(), dto.getQuantity(), member);

        Item savedItem = itemService.addItem(item, member);

        redirectAttributes.addAttribute("itemId", savedItem.getId());

        return "redirect:/items/{itemId}";
    }
}
