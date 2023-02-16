package com.example.springsecurityexam.service;

import com.example.springsecurityexam.domain.BuyItem;
import com.example.springsecurityexam.domain.Item;
import com.example.springsecurityexam.domain.Member;
import com.example.springsecurityexam.dto.item.ItemBuyDto;
import com.example.springsecurityexam.dto.member.LoginDto;
import com.example.springsecurityexam.dto.member.PasswordEditDto;
import com.example.springsecurityexam.dto.member.UserInfoEditDto;
import com.example.springsecurityexam.repository.BuyItemRepository;
import com.example.springsecurityexam.repository.ItemRepository;
import com.example.springsecurityexam.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.event.annotation.BeforeTestClass;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    BuyItemRepository buyItemRepository;

    @Autowired
    List<DeleteAccount> deleteAccount;

    private Member testUser;
    private MemberService memberService;

    @BeforeTestClass
    void clear(){
        itemRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
        buyItemRepository.deleteAllInBatch();
    }

    @BeforeEach
    void init(){
        testUser = new Member().createUserMember("test");
        memberService = new MemberService(memberRepository, bCryptPasswordEncoder, deleteAccount);
    }

    @Nested
    @DisplayName("회원가입")
    class signup {

        @Test
        @DisplayName("성공 로직")
        void signupSuccess(){
//            given

//            when
            boolean result = memberService.signup(testUser);

//            then
//            회원가입이 성공했는지를 확인
            assertThat(result).isTrue();

            Member findUser = memberRepository.findByUserId(testUser.getUserId())
                    .orElseThrow(NoSuchElementException::new);

//            비밀번호가 성공적으로 암호화가 되는지를 확인
            assertThat(findUser.getPassword()).isNotEqualTo(testUser.getPassword());
        }
    }

    @Nested
    @DisplayName("로그인")
    class login{

        @Test
        @DisplayName("폼 로그인 성공 로직")
        void formLoginSuccess() {
//            given

//            when
            memberService.signup(testUser);

            Member savedUser = memberRepository.findByUserId(testUser.getUserId())
                    .orElseThrow(NoSuchElementException::new);

            LoginDto loginDto = new LoginDto(savedUser.getUserId(), testUser.getPassword());

            Member loginUser = memberService.login(loginDto);

//            then
            assertThat(loginUser).isNotNull();

            assertThat(loginUser).usingRecursiveComparison()
                    .isEqualTo(savedUser);
        }

    }

    @Nested
    @DisplayName("계정 삭제")
    class delete{

        @Test
        @DisplayName("local 계정 성공 로직")
        void formDeleteSuccess() {
//            given
            memberService.signup(testUser);

            Member savedUser = memberRepository.findByUserId(testUser.getUserId())
                    .orElseThrow(NoSuchElementException::new);

//            when
            memberService.delete("form", testUser.getUserId(), testUser.getPassword());

            Member findUser = memberRepository.findByUserId(savedUser.getUserId())
                    .orElse(null);
//            then
            assertThat(findUser).isNull();
        }

    }

    @Nested
    @DisplayName("회원 정보 수정")
    class updateUserInfo {

        @Test
        @DisplayName("성공 로직")
        void formUserSuccess(){
//            given
            Member savedUser = memberRepository.save(testUser);

            String changeName = "seongmin";

            UserInfoEditDto userInfoEditDto = new UserInfoEditDto(changeName, savedUser.getEmail());

//            when
            Member updatedUser = memberService.updateUserInfo(savedUser.getUserId(), userInfoEditDto);

//            then
            assertThat(updatedUser.getName()).isEqualTo(changeName);

        }

    }

    @Nested
    @DisplayName("비밀번호 변경")
    class updatePassword {

        @Test
        @DisplayName("성공 로직")
        void updateSuccess(){
//            given
            Member savedUser = memberRepository.save(testUser);

            String password = "testPassword";
            String confirm = "testPassword";

            PasswordEditDto passwordEditDto = new PasswordEditDto(password, confirm);

//            when
            memberService.updatePassword(savedUser.getId(), passwordEditDto);

//            then
            assertThat(bCryptPasswordEncoder.matches(password, savedUser.getPassword()))
                    .isTrue();

        }

        @Test
        @DisplayName("실패 로직")
        void updateFail(){
//            given
            Member savedUser = memberRepository.save(testUser);

            String password = "testPassword";
            String confirm = "testConfirm";

            PasswordEditDto passwordEditDto = new PasswordEditDto(password, confirm);

//            when

//            then
            assertThrows(IllegalArgumentException.class, () -> {
                memberService.updatePassword(savedUser.getId(), passwordEditDto);
            });

        }

    }

    @Nested
    @DisplayName("구매 목록 찾기")
    class findBuyItems {

        @Test
        @DisplayName("성공 로직")
        void findSuccess(){
//            given
            memberService.signup(testUser);

            Member savedUser = memberRepository.findByUserId(testUser.getUserId())
                    .orElseThrow(NoSuchElementException::new);

            ItemService itemService = new ItemService(itemRepository, buyItemRepository, memberRepository);

            Item item = new Item("testItem", 1000, 1000, savedUser);

            itemRepository.save(item);
            Item savedItem = itemRepository.findByItemName(item.getItemName())
                    .orElseThrow(NoSuchElementException::new);
            int initial = savedItem.getQuantity();

            ItemBuyDto itemBuyDto = new ItemBuyDto(savedItem.getItemName(), savedItem.getPrice(), 5);

//            when
            itemService.buyItem(savedItem.getId(), savedUser.getUserId(), itemBuyDto);

            List<BuyItem> buyItems = savedUser.getBuyItems();

//            then
            assertThat(buyItems.size()).isEqualTo(1);
            assertThat(buyItems.get(0).getItem()).usingRecursiveComparison()
                    .isEqualTo(savedItem);
            assertThat(buyItems.get(0).getQuantity()).isEqualTo(5);
            assertThat(savedItem.getQuantity()).isEqualTo(initial-5);

        }
    }
}