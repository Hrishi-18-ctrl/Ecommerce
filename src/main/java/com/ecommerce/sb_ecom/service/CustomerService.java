package com.ecommerce.sb_ecom.service;


import com.ecommerce.sb_ecom.DTO.CustomerPageResponse;
import com.ecommerce.sb_ecom.DTO.CustomerRequest;
import com.ecommerce.sb_ecom.DTO.CustomerResponse;
import com.ecommerce.sb_ecom.exceptions.CustomerAlreadyExistsException;
import com.ecommerce.sb_ecom.exceptions.CustomerNotFoundException;
import com.ecommerce.sb_ecom.exceptions.UserNotFoundException;
import com.ecommerce.sb_ecom.model.Cart;
import com.ecommerce.sb_ecom.model.Customer;
import com.ecommerce.sb_ecom.model.User;
import com.ecommerce.sb_ecom.repository.CartRepository;
import com.ecommerce.sb_ecom.repository.CustomerRepository;
import com.ecommerce.sb_ecom.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;


//    GET ALL CUSTOMERS
    public CustomerPageResponse getAllCustomers(Integer pageNumber, Integer pageSize) {

        Pageable pageable = PageRequest.of(pageNumber , pageSize);

        Page<Customer> page = customerRepository.findAll(pageable);

        List<CustomerResponse> customerResponseList = page.getContent()
                .stream()
                .map(customer -> modelMapper.map(customer , CustomerResponse.class))
                .toList();

        CustomerPageResponse response = new CustomerPageResponse();
        response.setContent(customerResponseList);
        response.setPageNumber(page.getNumber());
        response.setPageSize(page.getSize());
        response.setTotalPages(page.getTotalPages());
        response.setTotalElements(page.getTotalElements());
        response.setLast(page.isLast());

        return response;
    }



//    CREATE CUSTOMER
    @Transactional
    public CustomerResponse createCustomer(CustomerRequest customerRequest) throws CustomerAlreadyExistsException {
        User user = userRepository.findById(customerRequest.getUserId())
                .orElseThrow(() -> new UserNotFoundException("user with id " +customerRequest.getUserId() + " not found"));

        if(user.getCustomer() != null){
            throw new CustomerAlreadyExistsException("Customer against user id " + user.getId() + " already exists");
        }

        Customer customer = new Customer();
        customer.setUser(user);
        customer.setFirstName(customerRequest.getFirstName());
        customer.setLastName(customerRequest.getLastName());
        customer.setGender(customerRequest.getGender());



        Customer savedCustomer = customerRepository.save(customer);

        //        CREATING CART OBJECT
        Cart cart = new Cart();
        cart.setCustomer(savedCustomer);
        cart.setTotalItems(0);
        cart.setTotalPrice(0.0);
        cart.setFinalPrice(0.0);
        cart.setDiscountAmount(0.0);
        Cart savedCart = cartRepository.save(cart);

        savedCustomer.setCart(savedCart);

        CustomerResponse response =  modelMapper.map(savedCustomer , CustomerResponse.class);
        response.setEmail(user.getEmail());
        response.setUser_id(user.getId());
        response.setCart_id(savedCustomer.getCart().getId());
        return response;
    }


//    UPDATE USER
    @Transactional
    public CustomerResponse updateCustomer(CustomerRequest customerRequest , String id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("customer with id " + id + " not found"));

        if(customerRequest.getFirstName() != null){
            customer.setFirstName(customerRequest.getFirstName());
        }

        if(customerRequest.getLastName() != null){
            customer.setLastName(customerRequest.getLastName());
        }

        if(customerRequest.getGender() != null){
            customer.setGender(customerRequest.getGender());
        }

        Customer savedCustomer = customerRepository.save(customer);

        CustomerResponse response =  modelMapper.map(savedCustomer , CustomerResponse.class);
        response.setEmail(savedCustomer.getUser().getEmail());
        response.setUser_id(savedCustomer.getUser().getId());
        return response;
    }
}
