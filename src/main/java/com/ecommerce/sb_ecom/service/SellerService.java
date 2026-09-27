package com.ecommerce.sb_ecom.service;


import com.ecommerce.sb_ecom.DTO.*;
import com.ecommerce.sb_ecom.exceptions.SellerNotFoundException;
import com.ecommerce.sb_ecom.exceptions.UserNotFoundException;
import com.ecommerce.sb_ecom.model.Seller;
import com.ecommerce.sb_ecom.model.User;
import com.ecommerce.sb_ecom.repository.SellerAddressRepository;
import com.ecommerce.sb_ecom.repository.SellerRepository;
import com.ecommerce.sb_ecom.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SellerService {

    private final SellerRepository sellerRepository;
    private final SellerAddressRepository sellerAddressRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;


//    GET ALL SELLERS
    public SellerPageResponse getAllSellers(Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber , pageSize);

        Page<Seller> page = sellerRepository.findAll(pageable);

        List<Seller> sellers = page.stream().toList();
        List<SellerResponse> sellerResponseList = new ArrayList<>();

        for(Seller seller : sellers){
            SellerResponse sellerResponse = new SellerResponse();
            sellerResponse.setId(seller.getId());
            sellerResponse.setFirstName(seller.getFirstName());
            sellerResponse.setLastName(seller.getLastName());
            sellerResponse.setEmail(seller.getUser().getEmail());
            sellerResponse.setUser_id(seller.getUser().getId());

            sellerResponseList.add(sellerResponse);
        }

        SellerPageResponse response = new SellerPageResponse();
        response.setContent(sellerResponseList);
        response.setPageNumber(page.getNumber());
        response.setPageSize(page.getSize());
        response.setTotalPages(page.getTotalPages());
        response.setTotalElements(page.getTotalElements());
        response.setLast(page.isLast());

        return response;
    }


//    CREATING A SELLER
    public SellerResponse createSeller(SellerRequest sellerRequest) {
        User user = userRepository.findById(sellerRequest.getUserId())
                .orElseThrow(() -> new UserNotFoundException("user with id " +sellerRequest.getUserId() + " not found"));

        if(user.getSeller() != null){
            throw new SellerNotFoundException("Seller against user id " + user.getId() + " already exists");
        }

        Seller seller = new Seller();
        seller.setUser(user);
        seller.setFirstName(sellerRequest.getFirstName());
        seller.setLastName(sellerRequest.getLastName());



        Seller savedSeller = sellerRepository.save(seller);


        SellerResponse response =  modelMapper.map(savedSeller , SellerResponse.class);
        response.setEmail(user.getEmail());
        response.setUser_id(user.getId());
        response.setFirstName(savedSeller.getFirstName());
        response.setLastName(savedSeller.getLastName());
        return response;
    }


//    UPDATING SELLER
    @Transactional
    public SellerResponse updateSeller(SellerRequest sellerRequest, String id) {
        Seller seller = sellerRepository.findById(id)
                .orElseThrow(() -> new SellerNotFoundException("seller with id " + id + " not found"));

        if(sellerRequest.getFirstName() != null){
            seller.setFirstName(sellerRequest.getFirstName());
        }

        if(sellerRequest.getLastName() != null){
            seller.setLastName(sellerRequest.getLastName());
        }

        Seller savedSeller = sellerRepository.save(seller);

        SellerResponse response =  modelMapper.map(savedSeller , SellerResponse.class);
        response.setEmail(savedSeller.getUser().getEmail());
        response.setUser_id(savedSeller.getUser().getId());
        response.setFirstName(savedSeller.getFirstName());
        response.setLastName(savedSeller.getLastName());
        return response;

    }
}
