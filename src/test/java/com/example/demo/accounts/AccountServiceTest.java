package com.example.demo.accounts;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AccountServiceTest {

    @Autowired
    AccountService accountService;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("인증정보가져오기")
    void findByUserName() {
        String email = "kisa0828@naver.com";
        Account account = Account.builder()
                .email(email)
                .password("123456789")
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();
        accountService.saveAccount(account);
        UserDetailsService userDetailsService = accountService;
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        assertThat(passwordEncoder.encode("123456789").equals(userDetails.getPassword()));
    }

    @Test
    @DisplayName("loadUserByUsername으로 불러오기 실패")
    void findByUserNameEmail() {
        String email = "lalalalal@naver.com";
        try {
            accountService.loadUserByUsername(email); // 못찾으면 notFound에러던지도록
            fail("계정찾기 실패");
        } catch (UsernameNotFoundException e) {
            assertThat(e.getMessage().contains(email));
        }
    }
}

