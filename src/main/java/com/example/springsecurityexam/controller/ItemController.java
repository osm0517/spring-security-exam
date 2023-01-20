package com.example.springsecurityexam.controller;

import com.example.springsecurityexam.domain.Item;
import com.example.springsecurityexam.dto.ItemAddDto;
import com.example.springsecurityexam.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@Slf4j
@RequestMapping("/items")
public class ItemController {

    private ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService){
        this.itemService = itemService;
    }

    @GetMapping
    public String itemsView(
            Model model
    ){
        List<Item> items = itemService.findItems();
        model.addAttribute("items", items);
        return "/items/items";
    }

    @GetMapping("/add")
    public String addItemView(
            ItemAddDto item,
            Model model
    ){
        model.addAttribute("item", item);
        return "/items/addForm";
    }

    @GetMapping("/{itemId}")
    public String itemView(
            @PathVariable int itemId,
            Model model
    ){


        Item item = itemService.findItem(itemId);
        model.addAttribute("item", item);
        return "/items/item";
    }

    @GetMapping("/edit/{itemId}")
    public String editView(
            @PathVariable int itemId,
            ItemAddDto item,
            Model model
    ){
        Item findItem = itemService.findItem(itemId);
        model.addAttribute("item", findItem);
        model.addAttribute("updateItem", item);
        return "/items/editForm";
    }

    @PostMapping("/edit/{itemId}")
    public String editItem(
            @PathVariable int itemId,
            ItemAddDto updateItem,
            RedirectAttributes redirectAttributes,
            Model model

    ){
        Item updatedItem = itemService.editItem(itemId, updateItem);
        model.addAttribute("item", updatedItem);
        redirectAttributes.addAttribute("itemId", updatedItem.getId());

        return "redirect:/items/{itemId}";
    }

    @PostMapping("/add")
    public String addItem(
            ItemAddDto item,
            RedirectAttributes redirectAttributes,
            Model model
    ){
        log.debug("modelAttribute = {} {} {}", item.getItemName(), item.getPrice(), item.getQuantity());

        if(checkAddDto(item)){

            model.addAttribute("result", "등록 내용을 다시 확인해주세요!");

            return "/items/fail";
        }

        Item savedItem = itemService.addItem(item);

        redirectAttributes.addAttribute("itemId", savedItem.getId());

        return "redirect:/items/{itemId}";
    }

    private static boolean checkAddDto(ItemAddDto item) {
        return item.getItemName() == null || item.getPrice() == 0 || item.getQuantity() == 0;
    }
}
