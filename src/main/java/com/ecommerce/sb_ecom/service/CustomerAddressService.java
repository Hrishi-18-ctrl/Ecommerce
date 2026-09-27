package com.ecommerce.sb_ecom.service;

import com.ecommerce.sb_ecom.DTO.AddressRequest;
import com.ecommerce.sb_ecom.DTO.AddressResponse;
import com.ecommerce.sb_ecom.exceptions.CustomerAddressNotFoundException;
import com.ecommerce.sb_ecom.exceptions.CustomerNotFoundException;
import com.ecommerce.sb_ecom.exceptions.DuplicateAddressTypeException;
import com.ecommerce.sb_ecom.model.Customer;
import com.ecommerce.sb_ecom.model.CustomerAddress;
import com.ecommerce.sb_ecom.repository.CustomerAddressRepository;
import com.ecommerce.sb_ecom.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class CustomerAddressService {

    private final CustomerAddressRepository customerAddressRepository;
    private final CustomerRepository customerRepository;





//    GET ALL ADDRESSES
    public List<AddressResponse> getAllAddress() {
        List<CustomerAddress> addressList =  customerAddressRepository.findAll();
        List<AddressResponse> addressResponseList = new ArrayList<>();

        for(CustomerAddress address : addressList){
            AddressResponse addressResponse = new AddressResponse();
            addressResponse.setId(address.getId());
            addressResponse.setCustomerId(address.getCustomer().getId());
            addressResponse.setAddressType(address.getType());
            addressResponse.setLine1(address.getLine1());
            addressResponse.setLine2(address.getLine2());
            addressResponse.setCity(address.getCity());
            addressResponse.setState(address.getState());
            addressResponse.setCountry(address.getCountry());
            addressResponse.setPinCode(address.getPinCode());
            addressResponse.setCreatedAt(address.getCreatedAt());
            addressResponse.setUpdatedAt(address.getUpdatedAt());

            addressResponseList.add(addressResponse);
        }

        return addressResponseList;
    }







//    CREATE CUSTOMER ADDRESS
    public AddressResponse createAddress(AddressRequest addressRequest) throws DuplicateAddressTypeException {
        Customer customer = customerRepository.findById(addressRequest.getCustomerId())
                .orElseThrow(() -> new CustomerNotFoundException("Customer with id " + addressRequest.getCustomerId() + " not found"));

        List<CustomerAddress> addressList = customer.getAddressList();

        if(!addressList.isEmpty()){
            for(CustomerAddress address:addressList){
                if(address.getType().toString().equalsIgnoreCase(addressRequest.getAddressType().toString())){
                    throw new DuplicateAddressTypeException("Duplicate address type creation ");
                }
            }
        }


        CustomerAddress address = new CustomerAddress();
        address.setType(addressRequest.getAddressType());
        address.setLine1(addressRequest.getLine1());
        address.setLine2(addressRequest.getLine2());
        address.setCity(addressRequest.getCity());
        address.setState(addressRequest.getState());
        address.setCountry(addressRequest.getCountry());
        address.setPinCode(addressRequest.getPinCode());
        address.setCustomer(customer);

        CustomerAddress savedCustomerAddress = customerAddressRepository.save(address);
        AddressResponse addressResponse = new AddressResponse();
        addressResponse.setId(savedCustomerAddress.getId());
        addressResponse.setCustomerId(savedCustomerAddress.getCustomer().getId());
        addressResponse.setAddressType(savedCustomerAddress.getType());
        addressResponse.setLine1(savedCustomerAddress.getLine1());
        addressResponse.setLine2(savedCustomerAddress.getLine2());
        addressResponse.setCity(savedCustomerAddress.getCity());
        addressResponse.setState(savedCustomerAddress.getState());
        addressResponse.setCountry(savedCustomerAddress.getCountry());
        addressResponse.setPinCode(savedCustomerAddress.getPinCode());
        addressResponse.setCreatedAt(savedCustomerAddress.getCreatedAt());
        addressResponse.setUpdatedAt(savedCustomerAddress.getUpdatedAt());


        return addressResponse;

    }






//    UPDATE CUSTOMER ADDRESS
    @Transactional
    public AddressResponse updateAddress(String id, AddressRequest addressRequest) {
//        customerAddress existing check
        CustomerAddress address = customerAddressRepository.findById(id)
                .orElseThrow(() -> new CustomerAddressNotFoundException("customer Address with id " + id + " not found"));

        if(addressRequest.getLine1() != null){
            address.setLine1(addressRequest.getLine1());
        }

        if(addressRequest.getLine2() != null){
            address.setLine2(addressRequest.getLine2());
        }

        if(addressRequest.getCity() != null){
            address.setCity(addressRequest.getCity());
        }

        if(addressRequest.getState() !=  null){
            address.setState(addressRequest.getState());
        }

        if(addressRequest.getCountry() != null){
            address.setCountry(addressRequest.getCountry());
        }

        if(addressRequest.getPinCode() != null){
            address.setPinCode(addressRequest.getPinCode());
        }



        CustomerAddress savedCustomerAddress = customerAddressRepository.save(address);

        AddressResponse addressResponse = new AddressResponse();
        addressResponse.setId(savedCustomerAddress.getId());
        addressResponse.setCustomerId(savedCustomerAddress.getCustomer().getId());
        addressResponse.setAddressType(savedCustomerAddress.getType());
        addressResponse.setLine1(savedCustomerAddress.getLine1());
        addressResponse.setLine2(savedCustomerAddress.getLine2());
        addressResponse.setCity(savedCustomerAddress.getCity());
        addressResponse.setState(savedCustomerAddress.getState());
        addressResponse.setCountry(savedCustomerAddress.getCountry());
        addressResponse.setPinCode(savedCustomerAddress.getPinCode());
        addressResponse.setCreatedAt(savedCustomerAddress.getCreatedAt());
        addressResponse.setUpdatedAt(savedCustomerAddress.getUpdatedAt());

        return addressResponse;
    }






//    DELETE CUSTOMER ADDRESS
    public void deleteAddress(String id) {
        //        customerAddress existing check
        CustomerAddress address = customerAddressRepository.findById(id)
                .orElseThrow(() -> new CustomerAddressNotFoundException("customer Address with id " + id + " not found"));

        customerAddressRepository.delete(address);

    }









//    GET ADDRESSES BY CUSTOMER ID
    public List<AddressResponse> getAddressesByCustomerId(String id) {
//        customer existing check
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("customer with id "+id + " not found"));

        List<CustomerAddress> addresses = customerAddressRepository.findByCustomerId(id);
        List<AddressResponse> addressResponseList = new ArrayList<>();

        if(addresses.isEmpty()){
            return addressResponseList;
        }


        for(CustomerAddress address : addresses){
            AddressResponse addressResponse = new AddressResponse();
            addressResponse.setId(address.getId());
            addressResponse.setCustomerId(address.getCustomer().getId());
            addressResponse.setAddressType(address.getType());
            addressResponse.setLine1(address.getLine1());
            addressResponse.setLine2(address.getLine2());
            addressResponse.setCity(address.getCity());
            addressResponse.setState(address.getState());
            addressResponse.setCountry(address.getCountry());
            addressResponse.setPinCode(address.getPinCode());
            addressResponse.setCreatedAt(address.getCreatedAt());
            addressResponse.setUpdatedAt(address.getUpdatedAt());

            addressResponseList.add(addressResponse);
        }

        return addressResponseList;

    }
}
