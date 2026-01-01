package com.subash.SGDisposals.repositories;

import com.subash.SGDisposals.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepo extends JpaRepository<Order, Integer> {

    List<Order> findById(Long id);

    List<Order> findByuserId(Long id);
}
