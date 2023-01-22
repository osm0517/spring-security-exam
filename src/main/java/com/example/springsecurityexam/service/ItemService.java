package com.example.springsecurityexam.service;

import com.example.springsecurityexam.domain.Item;
import com.example.springsecurityexam.dto.ItemAddDto;
import com.example.springsecurityexam.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class ItemService {

    private ItemRepository itemRepository;

    @Autowired
    public ItemService(ItemRepository itemRepository){
        this.itemRepository = itemRepository;
    }

    public Item addItem(ItemAddDto itemAddDto){
        log.debug("addItem item = {}", itemAddDto);

        Item item = new Item(itemAddDto.getItemName(), itemAddDto.getPrice(), itemAddDto.getQuantity());

        return itemRepository.save(item);
    }

    public Item editItem(int itemId, ItemAddDto itemAddDto){
        log.debug("addItem item = {}", itemAddDto);

        Item item = itemRepository.findById(itemId);
        item.updateItem(itemAddDto.getItemName(),
                itemAddDto.getPrice(),
                itemAddDto.getQuantity());

        return itemRepository.save(item);
    }

    public Item findItem(int itemId){

        return itemRepository.findById(itemId);
    }

    public List<Item> findItems(){

        return itemRepository.findAll();
    }
}
