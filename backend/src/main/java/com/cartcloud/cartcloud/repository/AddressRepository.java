package com.cartcloud.cartcloud.repository;

import com.cartcloud.cartcloud.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
  
}