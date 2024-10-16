package ru.tveu.shiftcrm.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tveu.shiftcrm.core.entity.Seller;

public interface SellerRepository extends JpaRepository<Seller, Long> {
}
