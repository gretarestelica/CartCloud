package com.cartcloud.cartcloud.service;

import com.cartcloud.cartcloud.model.Address;
import com.cartcloud.cartcloud.model.User;
import com.cartcloud.cartcloud.repository.AddressRepository;
import com.cartcloud.cartcloud.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public AddressService(AddressRepository addressRepository, UserRepository userRepository) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }

    public List<Address> getAllAddresses() {
        return addressRepository.findAll();
    }

    public Optional<Address> getAddressById(Long id) {
        return addressRepository.findById(id);
    }

  
    public Address createAddress(Long userId, Address address) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        address.setUser(user);
        return addressRepository.save(address);
    }

    public Address updateAddress(Long id, Address updated) {
        return addressRepository.findById(id)
                .map(existing -> {
                    
                    existing.setCity(updated.getCity());
                    existing.setCountry(updated.getCountry());
                    existing.setStreet(updated.getStreet());
                    existing.setPostalCode(updated.getPostalCode());
                    existing.setState(updated.getState());
                    return addressRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Address not found"));
    }

    public void deleteAddress(Long id) {
        addressRepository.deleteById(id);
    }
}
