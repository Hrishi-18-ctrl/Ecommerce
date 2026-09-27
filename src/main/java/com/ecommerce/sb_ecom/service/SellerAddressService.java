package com.ecommerce.sb_ecom.service;

import com.ecommerce.sb_ecom.DTO.AddressRequest;
import com.ecommerce.sb_ecom.DTO.AddressResponse;
import com.ecommerce.sb_ecom.DTO.SellerAddressRequest;
import com.ecommerce.sb_ecom.exceptions.DuplicateAddressTypeException;
import com.ecommerce.sb_ecom.exceptions.SellerAddressNotFoundException;
import com.ecommerce.sb_ecom.exceptions.SellerNotFoundException;
import com.ecommerce.sb_ecom.model.Customer;
import com.ecommerce.sb_ecom.model.CustomerAddress;
import com.ecommerce.sb_ecom.model.Seller;
import com.ecommerce.sb_ecom.model.SellerAddress;
import com.ecommerce.sb_ecom.repository.SellerAddressRepository;
import com.ecommerce.sb_ecom.repository.SellerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SellerAddressService {

    private final SellerAddressRepository sellerAddressRepository;
    private final SellerRepository sellerRepository;


//    GET ALL ADDRESSES
    public List<AddressResponse> getAllAddress() {
        List<SellerAddress> addressList =  sellerAddressRepository.findAll();
        List<AddressResponse> addressResponseList = new ArrayList<>();

        for(SellerAddress address : addressList){
            AddressResponse addressResponse = new AddressResponse();
            addressResponse.setId(address.getId());
            addressResponse.setSellerId(address.getSeller().getId());
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





//    CREATE ADDRESS
    public AddressResponse createAddress(SellerAddressRequest addressRequest) {
        Seller seller = sellerRepository.findById(addressRequest.getSellerId())
                .orElseThrow(() -> new SellerNotFoundException("Seller with id " + addressRequest.getSellerId() + " not found"));



        SellerAddress address = new SellerAddress();
        address.setLine1(addressRequest.getLine1());
        address.setLine2(addressRequest.getLine2());
        address.setCity(addressRequest.getCity());
        address.setState(addressRequest.getState());
        address.setCountry(addressRequest.getCountry());
        address.setPinCode(addressRequest.getPinCode());
        address.setSeller(seller);

        SellerAddress savedSellerAddress = sellerAddressRepository.save(address);
        AddressResponse addressResponse = new AddressResponse();
        addressResponse.setId(savedSellerAddress.getId());
        addressResponse.setSellerId(savedSellerAddress.getSeller().getId());
        addressResponse.setLine1(savedSellerAddress.getLine1());
        addressResponse.setLine2(savedSellerAddress.getLine2());
        addressResponse.setCity(savedSellerAddress.getCity());
        addressResponse.setState(savedSellerAddress.getState());
        addressResponse.setCountry(savedSellerAddress.getCountry());
        addressResponse.setPinCode(savedSellerAddress.getPinCode());
        addressResponse.setCreatedAt(savedSellerAddress.getCreatedAt());
        addressResponse.setUpdatedAt(savedSellerAddress.getUpdatedAt());

        return addressResponse;


    }


//    UPDATE ADDRESS
    public AddressResponse updateAddress(String id, AddressRequest addressRequest) {
        //        customerAddress existing check
        SellerAddress address = sellerAddressRepository.findById(id)
                .orElseThrow(() -> new SellerAddressNotFoundException("seller Address with id " + id + " not found"));

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


        SellerAddress savedSellerAddress = sellerAddressRepository.save(address);

        AddressResponse addressResponse = new AddressResponse();
        addressResponse.setId(savedSellerAddress.getId());
        addressResponse.setCustomerId(savedSellerAddress.getSeller().getId());
        addressResponse.setLine1(savedSellerAddress.getLine1());
        addressResponse.setLine2(savedSellerAddress.getLine2());
        addressResponse.setCity(savedSellerAddress.getCity());
        addressResponse.setState(savedSellerAddress.getState());
        addressResponse.setCountry(savedSellerAddress.getCountry());
        addressResponse.setPinCode(savedSellerAddress.getPinCode());
        addressResponse.setCreatedAt(savedSellerAddress.getCreatedAt());
        addressResponse.setUpdatedAt(savedSellerAddress.getUpdatedAt());

        return addressResponse;
    }


//    GET ADDRESS BY SELLER ID
    public AddressResponse getAddressesBySellerId(String id) {
        Seller seller = sellerRepository.findById(id)
                .orElseThrow(() -> new SellerNotFoundException("seller with id " +id + " not found"));

        SellerAddress address = seller.getAddress();

        AddressResponse addressResponse = new AddressResponse();
        addressResponse.setId(address.getId());
        addressResponse.setCustomerId(address.getSeller().getId());
        addressResponse.setLine1(address.getLine1());
        addressResponse.setLine2(address.getLine2());
        addressResponse.setCity(address.getCity());
        addressResponse.setState(address.getState());
        addressResponse.setCountry(address.getCountry());
        addressResponse.setPinCode(address.getPinCode());
        addressResponse.setCreatedAt(address.getCreatedAt());
        addressResponse.setUpdatedAt(address.getUpdatedAt());

        return addressResponse;
    }
}
