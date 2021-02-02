package com.example.demo.config;


import com.example.demo.accounts.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;

@Configuration
@EnableAuthorizationServer
@RequiredArgsConstructor
public class AuthServerConfig extends AuthorizationServerConfigurerAdapter {
    /**
     * 1. 인증을 위한 클라이언트 설정을 AuthServerConfig
     * 2. 리소스에 접근할 때  설정한 클라이언트를 통해 authorizationServer에 접근해서 accessToken - 토큰을 발급받고
     * 3. Resourceserver에서 리소스에 접근할 때 토큰을 발급받았는지 확인한 후 접근에대한 제어를 한다.
     * <p>
     * 따라서
     * <p>
     * ★ SecurityConfig에서는
     * - 토큰을 저장할 tokenStore설정
     * - AuthServer에서 인증을 위해 AuthenticationManager를 사용할 수 있도록 빈으로등록
     * - 이 떄 AuthenticationManager를 무엇을 사용해 빌드할지 configure(AuthenticationManagerBuilder auth)
     * <p>
     * ★ AuthServerConfig에서는 인증 SecurityConfig에서 설정한 정보를 바탕으로 토큰을 발급받을 클라이언트설정
     * - passwordEncoder
     * - clients 클라이언트 설정
     * - endpoints에서 authenticationManager / userDetailsService / tokenStore를 사용하겠다.를 지정
     *
     * 주의 - 지금은 작은 서비스이기때문에 인증서버와 리소스서버 분리x
     * inMemory TokenStore는 인증서버와 리소스서버가 분리되었을 때 토큰공유x -> 필요에따라 jdbc로 바꿔주기
     */


    private final AppProperties appProperties;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final AccountService accountService;
    private final TokenStore tokenStore;

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.passwordEncoder(passwordEncoder);
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        //클라이언트를 설정
        clients.inMemory()
                .withClient(appProperties.getClientId())
                .authorizedGrantTypes("password", "refresh_token")
                .scopes("read", "write")  /**   우리가 리소스서버에 대해 사용하고자하는 기능 */
                .secret(passwordEncoder.encode(appProperties.getClientSecret()))
                .accessTokenValiditySeconds(10 * 60)
                .refreshTokenValiditySeconds(6 * 10 * 60);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.authenticationManager(authenticationManager)
                .userDetailsService(accountService)
                .tokenStore(tokenStore);
    }
}
