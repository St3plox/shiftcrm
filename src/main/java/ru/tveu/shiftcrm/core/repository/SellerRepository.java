package ru.tveu.shiftcrm.core.repository;

import org.springframework.data.repository.CrudRepository;
import ru.tveu.shiftcrm.core.entity.Seller;

public interface SellerRepository extends CrudRepository<Seller, Long> {
}
