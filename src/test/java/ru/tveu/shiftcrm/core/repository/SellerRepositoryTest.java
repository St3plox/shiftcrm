package ru.tveu.shiftcrm.core.repository;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.tveu.shiftcrm.core.entity.Seller;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class SellerRepositoryTest {

    @Autowired
    private SellerRepository sellerRepository;

    @Test
    void shouldSaveAndRetrieveSeller() {
        Seller seller = Seller.builder()
                .name("Test Seller")
                .contactInfo("test@example.com")
                .build();

        Seller savedSeller = sellerRepository.save(seller);
        assertThat(savedSeller.getId()).isNotNull();
    }
}
