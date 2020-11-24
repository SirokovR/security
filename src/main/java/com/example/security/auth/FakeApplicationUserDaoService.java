package com.example.security.auth;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.example.security.sec.ApplicationUserRole.*;


@Repository("fake")
public class FakeApplicationUserDaoService  implements ApplicationUserDao{
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public FakeApplicationUserDaoService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Optional<ApplicationUser> selectApplicationUserByUsername(String username) {
        return getApplicationUser()
                .stream()
                .filter(applicationUser -> username.equals(applicationUser.getUsername()))
                .findFirst();
    }

    private List<ApplicationUser> getApplicationUser(){
        List<ApplicationUser> applicationUsers = Lists.newArrayList(
                new ApplicationUser(STUDENT.getGrantedAuthorities(),
                        passwordEncoder.encode("passwordA"),
                        "roman" ,
                        true,
                        true,
                        true),
                new ApplicationUser(STUDENT.getGrantedAuthorities(),
                        passwordEncoder.encode("password"),
                        "kaisaMakkarainen" ,
                        true,
                        true,
                        true),
                new ApplicationUser(ADMIN.getGrantedAuthorities(),
                        passwordEncoder.encode("password123"),
                        "linda" ,
                        true,
                        true,
                        true),
                new ApplicationUser(ADMINTRAINEE.getGrantedAuthorities(),
                        passwordEncoder.encode("password123"),
                        "tom" ,
                        true,
                        true,
                        true)

        );
        return applicationUsers;
    }


}
