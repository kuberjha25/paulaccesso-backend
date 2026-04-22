package com.paulaccesso.visitor.security;

import com.paulaccesso.visitor.entity.User;
import com.paulaccesso.visitor.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String empId) throws UsernameNotFoundException {
        User user = userRepository.findLoginUserByEmpId(empId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with EmpId: " + empId));

        return new org.springframework.security.core.userdetails.User(
                user.getEmpId(),
                "",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
        );
    }
}