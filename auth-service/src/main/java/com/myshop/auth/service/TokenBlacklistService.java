package com.myshop.auth.service;

import com.myshop.commons.constants.RedisKeyConstants;
import com.myshop.commons.exception.AppException;
import com.myshop.commons.exception.BusinessException;
import com.myshop.commons.exception.CommonMessageUtils;
import com.myshop.commons.exception.ErrorCode;
import com.myshop.commons.exception.MessageHandlerUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.HexFormat;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenBlacklistService {

    private final RedisTemplate<String, Object> redisTemplate;

    private String getRedisKey(String token) {
        return RedisKeyConstants.BLACKLIST_REFRESH_TOKEN_PREFIX + sha256(token);
    }

    public void blacklist(String token, Duration duration) {
        String key = getRedisKey(token);
        try {
            redisTemplate.opsForValue().set(key, true, duration);
        } catch (Exception e) {
            log.error("Failed to blacklist token: {}", e.getMessage());
            throw new BusinessException(
                    ErrorCode.INTERNAL_ERROR,
                    MessageHandlerUtils.getMessage(CommonMessageUtils.Auth.FAILED_BLACKLIST_TOKEN)
            );
        }
    }

    public boolean isBlacklisted(String token) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(getRedisKey(token)));
        } catch (Exception e) {
            log.warn("Redis blacklist check failed: {}", e.getMessage());
            return false;
        }
    }

    public void validate(String token) {
        if (isBlacklisted(token)) {
            throw new AppException(ErrorCode.TOKEN_REVOKED);
        }
    }

    public void storeRefreshToken(Long userId, String token) {
        String listKey = RedisKeyConstants.REFRESH_TOKENS_USER_PREFIX + userId;
        redisTemplate.opsForList().rightPush(listKey, token);
        log.info("Stored refresh token for user [{}]", userId);
    }

    public void revokeAllTokensForUser(Long userId, Duration ttl) {
        String listKey = RedisKeyConstants.REFRESH_TOKENS_USER_PREFIX + userId;
        List<Object> tokens = redisTemplate.opsForList().range(listKey, 0, -1);
        if (tokens != null && !tokens.isEmpty()) {
            for (Object tokenObj : tokens) {
                blacklist(tokenObj.toString(), ttl);
            }
            redisTemplate.delete(listKey);
        } else {
            log.info("No previous tokens found for user [{}]", userId);
        }
    }

    private static String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
