package com.ecommerce.sb_ecom.service;

import com.ecommerce.sb_ecom.DTO.AddressRequest;
import com.ecommerce.sb_ecom.DTO.AddressResponse;
import com.ecommerce.sb_ecom.DTO.SellerAddressRequest;
import com.ecommerce.sb_ecom.exceptions.InvalidRequestException;
import com.ecommerce.sb_ecom.exceptions.SellerAddressNotFoundException;
import com.ecommerce.sb_ecom.exceptions.SellerNotFoundException;
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
            addressResponseList.add(toResponse(address));
        }

        return addressResponseList;
    }


//    CREATE ADDRESS
    public AddressResponse createAddress(SellerAddressRequest addressRequest) {
        Seller seller = sellerRepository.findById(addressRequest.getSellerId())
                .orElseThrow(() -> new SellerNotFoundException("Seller with id " + addressRequest.getSellerId() + " not found"));

        // A seller has exactly one address (Seller.address is a @OneToOne). Without this
        // check a second create hit the database's unique constraint on seller_id and
        // surfaced as a bare 500.
        if (seller.getAddress() != null) {
            throw new InvalidRequestException("This seller already has an address. Edit it instead.");
        }

        SellerAddress address = new SellerAddress();
        address.setLine1(addressRequest.getLine1());
        address.setLine2(addressRequest.getLine2());
        address.setCity(addressRequest.getCity());
        address.setState(addressRequest.getState());
        address.setCountry(addressRequest.getCountry());
        address.setPinCode(addressRequest.getPinCode());
        address.setSeller(seller);

        return toResponse(sellerAddressRepository.save(address));
    }


//    UPDATE ADDRESS
    public AddressResponse updateAddress(String id, AddressRequest addressRequest) {
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

        return toResponse(sellerAddressRepository.save(address));
    }


//    GET ADDRESS BY SELLER ID
    public AddressResponse getAddressesBySellerId(String id) {
        Seller seller = sellerRepository.findById(id)
                .orElseThrow(() -> new SellerNotFoundException("seller with id " +id + " not found"));

        SellerAddress address = seller.getAddress();

        // Was: address.getId() on a null address -> NullPointerException -> 500 for every
        // seller who hasn't added an address yet (i.e. every new seller). A clear 404 lets
        // a client tell "no address yet" apart from a real server error.
        if (address == null) {
            throw new SellerAddressNotFoundException("seller with id " + id + " has no address yet");
        }

        return toResponse(address);
    }


    // One place that builds the response, so every endpoint fills the same fields.
    // (The old update/get code put the seller's id into customerId and left sellerId
    // null; both are now set from the address's seller.)
    private AddressResponse toResponse(SellerAddress address) {
        AddressResponse response = new AddressResponse();
        response.setId(address.getId());
        response.setSellerId(address.getSeller().getId());
        response.setLine1(address.getLine1());
        response.setLine2(address.getLine2());
        response.setCity(address.getCity());
        response.setState(address.getState());
        response.setCountry(address.getCountry());
        response.setPinCode(address.getPinCode());
        response.setCreatedAt(address.getCreatedAt());
        response.setUpdatedAt(address.getUpdatedAt());
        return response;
    }
}
