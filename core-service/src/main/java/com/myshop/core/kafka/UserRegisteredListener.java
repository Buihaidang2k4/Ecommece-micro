package com.myshop.core.kafka;

import com.myshop.core.constant.CoreKafkaConstants;
import com.myshop.commons.events.UserRegisteredEvent;
import com.myshop.core.entity.cart.Cart;
import com.myshop.core.entity.customer.UserProfile;
import com.myshop.core.repository.CartRepository;
import com.myshop.core.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserRegisteredListener {

    private final UserProfileRepository userProfileRepository;
    private final CartRepository cartRepository;

    @KafkaListener(
            topics = "${myshop.kafka.user-registered-topic:myshop.user.registered}",
            groupId = CoreKafkaConstants.CONSUMER_GROUP
    )
    @Transactional
    public void onUserRegistered(UserRegisteredEvent event) {
        log.info("Received UserRegisteredEvent: userId={}, email={}", event.getUserId(), event.getEmail());

        if (userProfileRepository.existsByUserId(event.getUserId())) {
            log.info("UserProfile already exists for userId={}, skipping", event.getUserId());
            return;
        }

        String username = event.getEmail() != null
                ? event.getEmail().split("@")[0]
                : "user_" + event.getUserId();

        UserProfile profile = UserProfile.builder()
                .userId(event.getUserId())
                .username(username)
                .build();
        userProfileRepository.save(profile);
        log.info("Created UserProfile profileId={} for userId={}", profile.getProfileId(), event.getUserId());

        Cart cart = Cart.builder()
                .profileId(profile.getProfileId())
                .totalPrice(BigDecimal.ZERO)
                .build();
        cartRepository.save(cart);
        log.info("Created empty Cart cartId={} for profileId={}", cart.getCartId(), profile.getProfileId());
    }
}
