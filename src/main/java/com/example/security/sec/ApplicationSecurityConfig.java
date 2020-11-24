package com.example.security.sec;

import com.example.security.auth.ApplicationUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.concurrent.TimeUnit;

import static com.example.security.sec.ApplicationUserRole.*;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)     //need for @PreAuthorize()  annotations
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {

    private final PasswordEncoder passwordEncoder;
    private final ApplicationUserService applicationUserService;

    @Autowired
    public ApplicationSecurityConfig(PasswordEncoder passwordEncoder,
                                     ApplicationUserService applicationUserService) {
        this.passwordEncoder = passwordEncoder;
        this.applicationUserService = applicationUserService;
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                /*.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())     redefine default generation
                .and()*/
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/", "index", "/css/*","/js/*").permitAll()
                .antMatchers("/api/**").hasRole(STUDENT.name())
                /*.antMatchers(HttpMethod.DELETE,"/management/api/**").hasAuthority(COURSE_WRITE.getPermission())
                .antMatchers(HttpMethod.POST,"/management/api/**").hasAuthority(COURSE_WRITE.getPermission())
                .antMatchers(HttpMethod.PUT,"/management/api/**").hasAuthority(COURSE_WRITE.getPermission())
                .antMatchers(HttpMethod.GET,"/management/api/**").hasAnyRole(ADMIN.name(), ADMINTRAINEE.name())*/
                .anyRequest()
                .authenticated()
                .and()
                //.httpBasic();    // thats - Basic authentication
                .formLogin()    //  thats - Form base authentication
                    .loginPage("/login")
                    .permitAll()
                    .defaultSuccessUrl("/courses",true)
                    .passwordParameter("password")  //name from login.html
                    .usernameParameter("username")  // name from login.html
                .and()
                .rememberMe()
                    .tokenValiditySeconds((int) TimeUnit.DAYS.toSeconds(30))
                    .rememberMeParameter("remember-me") //name from login.html
                .key("mysupersecretkeyforthis") //New custom period for cookie holding
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout","GET")) //we MUST to do that because we have csrf()disable(). Or need to remove this line of code
                .clearAuthentication(true)
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID", "remember-me")
                .logoutSuccessUrl("/login");
    }


    /*@Override
    @Bean
    protected UserDetailsService userDetailsService() {
      UserDetails kaisaMakkarainenUser  = User.builder()
                .username("kaisaMakkarainen")
                .password(passwordEncoder.encode("password")) // now it looks like BCryptPa
                 .authorities(STUDENT.name())
               //  .roles(STUDENT.name())  //ROLE_STUDENT
              .authorities(STUDENT.getGrantedAuthorities())
                .build();

      UserDetails romanUser = User.builder()
              .username("roman")
              .password(passwordEncoder.encode("passwordA"))
              //.roles(STUDENT.name())
              .authorities(STUDENT.getGrantedAuthorities())
              .build();


        UserDetails lindaUser = User.builder()
                .username("linda")
                .password(passwordEncoder.encode("password123"))
                //.roles(ADMIN.name())
                .authorities(ADMIN.getGrantedAuthorities())
                .build();


        UserDetails tomUser = User.builder()
                .username("tom")
                .password(passwordEncoder.encode("password123"))
                //.roles(ADMINTRAINEE.name())
                .authorities(ADMINTRAINEE.getGrantedAuthorities())
                .build();



        return new InMemoryUserDetailsManager(
                kaisaMakkarainenUser,
                romanUser,
                lindaUser,
                tomUser
        );
    }*/

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(daoAuthenticationProvider());
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(){
        DaoAuthenticationProvider provider= new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(applicationUserService);
        return provider;

    }

}
