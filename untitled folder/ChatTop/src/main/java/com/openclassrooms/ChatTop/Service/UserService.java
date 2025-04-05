package com.openclassrooms.ChatTop.Service;

import com.openclassrooms.ChatTop.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    public UserRepository userRepository;


}
