package com.usermodule.usermodule.services;

import com.usermodule.usermodule.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class MyUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        Optional<com.usermodule.usermodule.dto.User> userData = userRepository.findById(Integer.parseInt(userId));

        if (userData == null) {
            throw new UsernameNotFoundException(userId);
        }
        return new User(userId, userData.get().getPassword(), new ArrayList<>());
    }

}
