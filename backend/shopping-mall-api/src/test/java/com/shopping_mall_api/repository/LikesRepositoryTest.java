package com.shopping_mall_api.repository;

import com.shopping_mall_api.dto.likes.LikedProductResponseDTO;
import com.shopping_mall_api.dto.product.productDetail.ProductDetailCreateDTO;
import com.shopping_mall_api.entity.likes.Likes;
import com.shopping_mall_api.entity.product.Product;
import com.shopping_mall_api.entity.user.User;
import com.shopping_mall_api.global.config.JpaAuditingConfig;
import com.shopping_mall_api.repository.likes.LikesRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import(JpaAuditingConfig.class)
class LikesRepositoryTest {
    @Autowired private LikesRepository likesRepository;
    @Autowired private EntityManager em;

    private User user;
    private User other;
    private Product potato;
    private Product apple;

    @BeforeEach
    void setUp(){
        user = persistUser("a@gmail.com");
        other = persistUser("b@gmail.com");
        potato = persistProduct("감자");
        apple = persistProduct("사과");
        em.flush();
    }

    private User persistUser(String email){
        User u = User.builder().email(email).logInPassword("pw").name("김민준").role("USER")
                .phoneNumber("01011111111").birthday(LocalDate.of(2026, 8, 27)).build();
        em.persist(u);
        return u;
    }

    private Product persistProduct(String name){
        Product p = Product.builder().name(name).price(1000L).stock(10L)
                .productDetailCreateDTOList(List.of(new ProductDetailCreateDTO("detail"))).build();
        em.persist(p);
        return p;
    }

    @Test
    void sameUserAndProduct_cannotBeLikedTwice(){
        likesRepository.saveAndFlush(new Likes(user, potato));

        assertThatThrownBy(() -> likesRepository.saveAndFlush(new Likes(user, potato)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void myLikes_newestFirst_withProductLoaded(){
        likesRepository.save(new Likes(user, potato));
        likesRepository.save(new Likes(user, apple));
        likesRepository.save(new Likes(other, apple));
        em.flush();
        em.clear();

        List<LikedProductResponseDTO> likes = likesRepository.findByUser_UserIdOrderByIdDesc(user.getUserId()).stream()
                .map(LikedProductResponseDTO::new).toList();

        assertThat(likes).extracting(LikedProductResponseDTO::name).containsExactly("사과", "감자");
        assertThat(likesRepository.countByProduct_ProductId(apple.getProductId())).isEqualTo(2L);
    }

    @Test
    void deleteQueries(){
        likesRepository.save(new Likes(user, potato));
        likesRepository.save(new Likes(user, apple));
        likesRepository.save(new Likes(other, apple));
        em.flush();

        assertThat(likesRepository.deleteByUserIdAndProductId(user.getUserId(), potato.getProductId())).isEqualTo(1);
        assertThat(likesRepository.deleteByUserIdAndProductId(user.getUserId(), potato.getProductId())).isZero();
        assertThat(likesRepository.deleteAllByProductId(apple.getProductId())).isEqualTo(2);
        assertThat(likesRepository.count()).isZero();
    }
}
