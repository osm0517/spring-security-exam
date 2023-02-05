package com.example.springsecurityexam.service;

import com.example.springsecurityexam.domain.BuyItem;
import com.example.springsecurityexam.domain.Item;
import com.example.springsecurityexam.domain.Member;
import com.example.springsecurityexam.dto.item.ItemBuyDto;
import com.example.springsecurityexam.dto.item.ItemUpdateDto;
import com.example.springsecurityexam.repository.BuyItemRepository;
import com.example.springsecurityexam.repository.ItemRepository;
import com.example.springsecurityexam.repository.MemberRepository;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final BuyItemRepository buyItemRepository;
    private final MemberRepository memberRepository;

    public Item addItem(Item item, Member member){
        return itemRepository.save(item);
    }

    public Item editItem(long itemId, ItemUpdateDto itemSaveDto){
        log.debug("addItem item = {}", itemSaveDto);

        Item item = itemRepository.findById(itemId)
                .orElseThrow(NoSuchElementException::new);

        item.updateItem(itemSaveDto.getItemName(),
                itemSaveDto.getPrice(),
                itemSaveDto.getQuantity());

        return itemRepository.save(item);
    }

    public Item findItem(long itemId){

        return itemRepository.findById(itemId)
                .orElseThrow(NoSuchElementException::new);
    }

    public ItemBuyDto findBuyItem(long itemId){

         Item item = itemRepository.findById(itemId)
                .orElseThrow(NoSuchElementException::new);

        return new ItemBuyDto(item.getItemName(), item.getPrice(), item.getQuantity());
    }

    public void buyItem(long itemId, long userId, ItemBuyDto dto) throws Exception {

        Item item = itemRepository.findById(itemId)
                .orElseThrow(NoSuchElementException::new);

        if(item.getQuantity() - dto.getBuyQuantity() < 0){
            throw new Exception("재고보다 큰 수를 입력함");
        }

        Member member = memberRepository.findById(userId)
                .orElseThrow(NoSuchElementException::new);

        BuyItem buyItem = new BuyItem(member, item, dto.getBuyQuantity());

        itemRepository.save(item);
        buyItemRepository.save(buyItem);

        member.getBuyItems().add(buyItem);
    }

    public Page<Item> findOtherItems(
            @Nonnull int page, @Nonnull int size, @Nonnull Member producer
    ){

        PageRequest pageRequest = PageRequest.of(page, size);

        return itemRepository.findAllByProducerNot(producer, pageRequest);
    }

    public Page<Item> findMyItems(
            @Nonnull int page, @Nonnull int size, @Nonnull Member producer
    ){

        PageRequest pageRequest = PageRequest.of(page, size);

        return itemRepository.findAllByProducer(producer, pageRequest);
    }

    public int paginationCount(
            @Nonnull int number, @Nullable Member producer
    ){
        long countValue;
        if(producer == null){
            countValue = itemRepository.count();
        }else{
            countValue = itemRepository.countByProducer(producer);
        }
        return (int)Math.ceil((float)countValue/number);
    }

    public void deleteItem(long itemId){
        itemRepository.deleteById(itemId);
    }
}
