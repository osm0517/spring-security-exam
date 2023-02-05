package com.example.springsecurityexam.controller;

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

import java.security.Principal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@Slf4j
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

//    ----- view path start -----

    String otherItemsPath = "/items/items";
    String addFormPath = "/items/addForm";
    String itemFormPath = "/items/item";
    String producerInfoPath = "/member/producer/info";
    String producerItemsPath = "/member/producer/items";
    String itemBuyPath = "/items/buyForm";
    String itemEditFormPath = "/items/editForm";
    String errorFormPath = "error";

//    ----- view path end -----

    private final ItemService itemService;
    private final MemberService memberService;

    private int size = 5;

    /**
     * 내가 판매하는 품목을 제외한 품목의 목록을 보여줌
     */
    @GetMapping
    public String itemsView(
            @RequestParam int page,
            Model model,
            Principal principal
    ){

        String userId = principal.getName();
        Member member = memberService.checkUserId(userId);
//        paging한 품목
        Page<Item> items = itemService.findOtherItems(page-1, size, member);
//        페이지 버튼을 어디까지 만들어야 하는지를 구함
        int pagination = itemService.otherItemsPaginationCount(size, member);

        toPagingSetModel(model, items, pagination);

        return otherItemsPath;

    }

    /**
     * 상품 등록 폼을 보여줌
     */
    @GetMapping("/add")
    public String addItemView(
            Model model
    ){
        model.addAttribute("item", new ItemSaveDto());

        return addFormPath;
    }

    /**
     * 상품 등록 로직을 처리
     */
    @PostMapping("/add")
    public String addItem(
            @Validated @ModelAttribute(name = "item") ItemSaveDto dto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Principal principal
    ){

        if(bindingResult.hasErrors()){
            log.debug("error = {}", bindingResult);
            return addFormPath;
        }

        String userId = principal.getName();

        Member member = memberService.checkUserId(userId);

        Item item = new Item(dto.getItemName(), dto.getPrice(), dto.getQuantity(), member);

        Item savedItem = itemService.addItem(item, member);

        redirectAttributes.addAttribute("itemId", savedItem.getId());

        return "redirect:/items/{itemId}";
    }


    @GetMapping("/{itemId}")
    public String itemView(
            @PathVariable int itemId,
            Model model
    ){

        Item item = itemService.findItem(itemId);
        model.addAttribute("item", item);
        model.addAttribute("producer", item.getProducer());

        return itemFormPath;
    }

    @GetMapping("/{itemId}/producer/info")
    public String producerInfoForm(
            @PathVariable int itemId,
            Model model
    ){

        Item item = itemService.findItem(itemId);

        model.addAttribute("itemId", itemId);
        model.addAttribute("producer", item.getProducer());

        return producerInfoPath;
    }

    @GetMapping("/{itemId}/producer/info/items")
    public String producerItemsForm(
            @PathVariable int itemId,
            @RequestParam int page,
            Model model
    ){

        Item item = itemService.findItem(itemId);
        Member producer = item.getProducer();
        Page<Item> items = itemService.findMyItems(page-1, size, producer);
        int paging = itemService.myItemsPaginationCount(size, producer);

        toPagingSetModel(model, items, paging);

        model.addAttribute("producer", producer);

        return producerItemsPath;
    }

    @GetMapping("/{itemId}/buy/popup")
    public String buyForm(
            @PathVariable int itemId,
            Model model
    ){
        Item item = itemService.findItem(itemId);

        model.addAttribute("item", item);
        model.addAttribute("dto", new ItemBuyDto());

        return itemBuyPath;
    }

    /**
     * 구매 로직
     * 오류가 있을 때 구매가 안되고 중간에 플로우가 끊김
     */
    @PostMapping("/{itemId}/buy/popup")
    public void buyItem(
            @PathVariable long itemId,
            @Validated @ModelAttribute ItemBuyDto dto,
            BindingResult bindingResult,
            Principal principal
    ){
        log.debug("buy debug");

        if(dto.getQuantity() > 1 && dto.getBuyQuantity() > dto.getQuantity()){
            bindingResult.reject("soldOut");
        }

        if(bindingResult.hasErrors()){
            log.debug("error = {}", bindingResult);
            return;
        }

        try {
            String userId = principal.getName();

            itemService.buyItem(itemId, userId, dto);
        }catch (NoSuchElementException e) {
            log.debug("db에 해당하는 id가 없음");
        }catch (Exception e){
            log.debug(e.getMessage());
        }
    }

    /**
     * 내 아이템을 수정함
     */
    @GetMapping("/edit/{itemId}")
    public String editView(
            @PathVariable int itemId,
            Model model,
            Principal principal
    ){
        Item findItem = itemService.findItem(itemId);

        String userId = principal.getName();
        String producer = findItem.getProducer().getUserId();

        if(hasProducerError(producer, userId)){
            return errorFormPath;
        }

        model.addAttribute("item", new ItemUpdateDto(findItem.getId(), findItem.getItemName(), findItem.getPrice(), findItem.getQuantity()));

        return itemEditFormPath;
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
            return itemEditFormPath;
        }

        Item updatedItem = itemService.editItem(itemId, updateItem);

        model.addAttribute("item", updatedItem);

        redirectAttributes.addAttribute("itemId", updatedItem.getId());

        return "redirect:/items/{itemId}";
    }

    /**
     * 내가 올린 물품을 삭제하는 로직
     */
    @PostMapping("/delete/{itemId}")
    public String deleteItem(
            @PathVariable long itemId,
            Principal principal
    ){
        Item item = itemService.findItem(itemId);

        String producer = item.getProducer().getName();
        String userId = principal.getName();

        if(hasProducerError(producer, userId)){
            return errorFormPath;
        }
        itemService.deleteItem(itemId);

        return "redirect:/profile/produce/items";
    }

    /**
     * paging을 할 수 있도록 해줌
     * model에 값도 담아줌
     */
    private static void toPagingSetModel(Model model, Page<Item> items, int paging) {
        List<Integer> range = IntStream.rangeClosed(1, paging)
                .boxed()
                .collect(Collectors.toList());

        model.addAttribute("items", items);
        model.addAttribute("paging", range);
        model.addAttribute("max", paging);
    }

    private static boolean hasProducerError(String producer, String userId){
        return !Objects.equals(producer, userId);
    }
}
