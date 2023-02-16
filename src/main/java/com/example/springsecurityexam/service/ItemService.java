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

    public void buyItem(long itemId, String userId, ItemBuyDto dto) {

        Item item = itemRepository.findById(itemId)
                .orElseThrow(NoSuchElementException::new);

        Member member = memberRepository.findByUserId(userId)
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

        return itemRepository.findByProducerNot(producer, pageRequest);
    }

    public Page<Item> findMyItems(
            @Nonnull int page, @Nonnull int size, @Nonnull Member producer
    ){

        PageRequest pageRequest = PageRequest.of(page, size);

        return itemRepository.findByProducer(producer, pageRequest);
    }

    /**
     * 페이지를 옮길 수 있는 버튼이 어디까지 표시를 해줘야하는지 구해줌
     */
    public int otherItemsPaginationCount(
            @Nonnull int number, @Nullable Member producer
    ){
        long countValue = itemRepository.countByProducerNot(producer);
        return (int)Math.ceil((float)countValue/number);
    }

    /**
     * 페이지를 옮길 수 있는 버튼이 어디까지 표시를 해줘야하는지 구해줌
     */
    public int myItemsPaginationCount(
            @Nonnull int number, @Nullable Member producer
    ){
        long countValue = itemRepository.countByProducer(producer);
        return (int)Math.ceil((float)countValue/number);
    }

    public void deleteItem(long itemId){
        itemRepository.deleteById(itemId);
    }
}
