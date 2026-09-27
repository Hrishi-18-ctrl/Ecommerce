package com.ecommerce.sb_ecom.service;

import com.ecommerce.sb_ecom.DTO.UserPageResponse;
import com.ecommerce.sb_ecom.DTO.UserRequest;
import com.ecommerce.sb_ecom.DTO.UserResponse;
import com.ecommerce.sb_ecom.exceptions.EmailAlreadyExistsException;
import com.ecommerce.sb_ecom.exceptions.UserNotFoundException;
import com.ecommerce.sb_ecom.model.User;
import com.ecommerce.sb_ecom.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;


//    GET ALL USERS
    public UserPageResponse getAllUsers(Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber , pageSize);
        Page<User> page = userRepository.findAll(pageable);

        List<UserResponse> users = page.getContent()
                .stream()
                .map(user -> modelMapper.map(user , UserResponse.class))
                .toList();

        UserPageResponse response = new UserPageResponse();
        response.setContent(users);
        response.setPageNumber(page.getNumber());
        response.setPageSize(page.getSize());
        response.setTotalPages(page.getTotalPages());
        response.setTotalElements(page.getTotalElements());
        response.setLast(page.isLast());

        return response;
    }


//    CREATE USER
    public UserResponse createUser(UserRequest userRequest) {
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new EmailAlreadyExistsException("An account with email " + userRequest.getEmail() + " already exists");
        }

        User user = modelMapper.map(userRequest , User.class);
        user.setPassword(passwordEncoder.encode(userRequest.getPassword())); // never persist raw passwords
        User savedUser = userRepository.save(user);
        return modelMapper.map(savedUser , UserResponse.class);
    }




//    UPDATE USER
    @Transactional
    public UserResponse updateUser(String id , UserRequest userRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id "+ id + " not found"));
        if(userRequest.getEmail() != null){
            user.setEmail(userRequest.getEmail());
        }

        if(userRequest.getPassword() != null){
            user.setPassword(passwordEncoder.encode(userRequest.getPassword())); // never persist raw passwords
        }

        return modelMapper.map(user , UserResponse.class);
    }




    //    DELETE USER
    public void deleteUser(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id +" not found"));
        userRepository.delete(user);
    }



}
