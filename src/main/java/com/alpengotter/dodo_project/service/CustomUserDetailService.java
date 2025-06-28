package com.alpengotter.dodo_project.service;

import com.alpengotter.dodo_project.domain.entity.UserEntity;
import com.alpengotter.dodo_project.domain.repository.UserRepository;
import com.alpengotter.dodo_project.handler.ErrorType;
import com.alpengotter.dodo_project.handler.exception.LemonBankException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
//@Lazy
public class CustomUserDetailService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<UserEntity> user = userRepository.findByEmailContainingIgnoreCaseAndIsActiveIsTrue(email);
        if (user.isPresent())
        {
            UserEntity userObj = user.get();
            return User.builder()
                .username(userObj.getEmail())
                .password(userObj.getPassword())
                .roles(userObj.getUserRole())
                .build();
        }
        else {
            throw new LemonBankException(ErrorType.USER_NOT_FOUND);
        }
    }
}
