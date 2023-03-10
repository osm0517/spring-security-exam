package com.example.springsecurityexam.auth.userDetails;

import com.example.springsecurityexam.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final Member member;

    public long getId(){
        return member.getId();
    }

    public String getEmail(){
        return member.getEmail();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> collect = new ArrayList<>();
        GrantedAuthority grantedAuthority = () -> member.getAuth().toString();
        collect.add(grantedAuthority);
        return collect;
    }

    @Override
    public String getPassword() {
        return member.getPassword();
    }

    @Override
    public String getUsername() {
        return member.getUserId();
    }

    /**
     * 계정 만료 여부
     * true : 만료 안됨
     * false : 만료됨
     * @return
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 계정 잠김 여부
     * true : 잠기지 않음
     * false : 잠김
     * @return
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 비밀번호 만료 여부
     * true : 만료 안됨
     * false : 만료됨
     * @return
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 계정 활성화 여부
     * true : 활성화
     * false : 잠김
     * @return
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}
