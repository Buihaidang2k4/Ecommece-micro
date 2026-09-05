package com.myshop.payment.service;

import com.myshop.commons.constants.enums.CommonEnums.PaymentMethod;
import com.myshop.commons.constants.enums.CommonEnums.PaymentStatus;
import com.myshop.commons.events.PaymentExpiredEvent;
import com.myshop.commons.events.PaymentFailedEvent;
import com.myshop.commons.events.PaymentSucceededEvent;
import com.myshop.commons.exception.BusinessException;
import com.myshop.commons.exception.CommonMessageUtils;
import com.myshop.commons.exception.ErrorCode;
import com.myshop.commons.exception.MessageHandlerUtils;
import com.myshop.payment.config.VnpayProperties;
import com.myshop.payment.constant.VnpayConstants;
import com.myshop.payment.dto.CreatePaymentRequest;
import com.myshop.payment.dto.PaymentInitResponse;
import com.myshop.payment.dto.PaymentResponse;
import com.myshop.payment.entity.Payment;
import com.myshop.payment.kafka.PaymentEventPublisher;
import com.myshop.payment.repository.PaymentRepository;
import com.myshop.payment.util.VnpayUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private static final int VNPAY_EXPIRE_HOURS = 12;
    private static final int AMOUNT_MULTIPLIER = 100;
    private static final int TXN_REF_RANDOM_LENGTH = 6;

    private final PaymentRepository paymentRepository;
    private final VnpayProperties vnpayProperties;
    private final PaymentEventPublisher eventPublisher;

    @Transactional
    public PaymentResponse createPayment(CreatePaymentRequest request) {
        int method = request.getPaymentMethod();
        int status;
        LocalDateTime expiredAt = null;
        String vnpTxnRef = null;

        if (method == PaymentMethod.CASH) {
            status = PaymentStatus.UNPAID;
        } else {
            status = PaymentStatus.INIT;
            expiredAt = LocalDateTime.now().plusHours(VNPAY_EXPIRE_HOURS);
            vnpTxnRef = request.getOrderId() + "-" + VnpayUtil.getRandomNumber(TXN_REF_RANDOM_LENGTH);
        }

        Payment payment = Payment.builder()
                .orderId(request.getOrderId())
                .paymentMethod(method)
                .paymentStatus(status)
                .amount(request.getAmount())
                .orderInfo(VnpayConstants.ORDER_INFO_PREFIX + request.getOrderId())
                .bankCode(request.getBankCode())
                .vnpTxnRef(vnpTxnRef)
                .expiredAt(expiredAt)
                .build();

        paymentRepository.save(payment);
        return toResponse(payment);
    }

    @Transactional(readOnly = true)
    public PaymentInitResponse createVnPayRedirect(Long paymentId, HttpServletRequest httpRequest) {
        Payment payment = findPaymentById(paymentId);

        if (!Objects.equals(payment.getPaymentMethod(), PaymentMethod.VNPAY)) {
            throw new BusinessException(
                    ErrorCode.VALIDATION_ERROR,
                    MessageHandlerUtils.getMessage(CommonMessageUtils.Payment.ONLY_VNPAY_REDIRECT));
        }
        if (!Objects.equals(payment.getPaymentStatus(), PaymentStatus.INIT)) {
            throw new BusinessException(
                    ErrorCode.VALIDATION_ERROR,
                    MessageHandlerUtils.getMessage(CommonMessageUtils.Payment.ALREADY_PROCESSED));
        }
        if (payment.getExpiredAt() != null && payment.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException(
                    ErrorCode.VALIDATION_ERROR,
                    MessageHandlerUtils.getMessage(CommonMessageUtils.Payment.EXPIRED));
        }

        long amountInSmallestUnit = payment.getAmount()
                .multiply(BigDecimal.valueOf(AMOUNT_MULTIPLIER))
                .setScale(0, RoundingMode.HALF_UP)
                .longValueExact();

        Map<String, String> params = vnpayProperties.getVNPayConfig();
        params.put(VnpayConstants.Param.AMOUNT, String.valueOf(amountInSmallestUnit));
        params.put(VnpayConstants.Param.TXN_REF, payment.getVnpTxnRef());
        params.put(VnpayConstants.Param.ORDER_INFO, payment.getOrderInfo());
        params.put(VnpayConstants.Param.IP_ADDR, VnpayUtil.getIpAddress(httpRequest));
        if (payment.getBankCode() != null && !payment.getBankCode().isBlank()) {
            params.put(VnpayConstants.Param.BANK_CODE, payment.getBankCode());
        }

        String queryUrl = VnpayUtil.getPaymentURL(params, true);
        String hashData = VnpayUtil.getPaymentURL(params, false);
        String secureHash = VnpayUtil.hmacSHA512(vnpayProperties.getSecretKey(), hashData);
        queryUrl += VnpayConstants.QUERY_SECURE_HASH_PREFIX + secureHash;

        String redirectUrl = vnpayProperties.getUrl() + "?" + queryUrl;

        return PaymentInitResponse.builder()
                .paymentId(payment.getPaymentId())
                .redirectUrl(redirectUrl)
                .paymentStatus(payment.getPaymentStatus())
                .build();
    }

    @Transactional
    public PaymentResponse handleVnPayCallback(Map<String, String> queryParams) {
        String vnpSecureHash = queryParams.get(VnpayConstants.Param.SECURE_HASH);

        Map<String, String> filtered = new TreeMap<>(queryParams);
        filtered.remove(VnpayConstants.Param.SECURE_HASH);
        filtered.remove(VnpayConstants.Param.SECURE_HASH_TYPE);

        String hashData = VnpayUtil.getPaymentURL(filtered, false);
        String expectedHash = VnpayUtil.hmacSHA512(vnpayProperties.getSecretKey(), hashData);

        if (!expectedHash.equalsIgnoreCase(vnpSecureHash)) {
            log.warn("VNPay callback hash mismatch — possible tampering. TxnRef={}",
                    queryParams.get(VnpayConstants.Param.TXN_REF));
            throw new BusinessException(
                    ErrorCode.VALIDATION_ERROR,
                    MessageHandlerUtils.getMessage(CommonMessageUtils.Payment.INVALID_SIGNATURE));
        }

        String vnpTxnRef = queryParams.get(VnpayConstants.Param.TXN_REF);
        String responseCode = queryParams.get(VnpayConstants.Param.RESPONSE_CODE);

        Payment payment = paymentRepository.findByVnpTxnRef(vnpTxnRef)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        MessageHandlerUtils.getMessage(CommonMessageUtils.Payment.NOT_FOUND)));

        if (Objects.equals(payment.getPaymentStatus(), PaymentStatus.PAID)) {
            return toResponse(payment);
        }

        payment.setResponseCode(responseCode);
        payment.setTransactionNo(queryParams.get(VnpayConstants.Param.TRANSACTION_NO));
        payment.setCardType(queryParams.get(VnpayConstants.Param.CARD_TYPE));
        payment.setOrderInfo(queryParams.get(VnpayConstants.Param.ORDER_INFO));
        payment.setBankCode(queryParams.get(VnpayConstants.Param.BANK_CODE));

        if (VnpayConstants.RESPONSE_SUCCESS.equals(responseCode)) {
            payment.setPaymentStatus(PaymentStatus.PAID);
            payment.setStatus(VnpayConstants.STATUS_SUCCESS);
            payment.setPaymentDate(LocalDateTime.now());
            paymentRepository.save(payment);

            eventPublisher.publishSucceeded(PaymentSucceededEvent.builder()
                    .paymentId(payment.getPaymentId())
                    .orderId(payment.getOrderId())
                    .paymentMethod(payment.getPaymentMethod())
                    .amount(payment.getAmount())
                    .transactionNo(payment.getTransactionNo())
                    .occurredAt(Instant.now())
                    .build());
        } else {
            payment.setPaymentStatus(PaymentStatus.FAILED);
            payment.setStatus(VnpayConstants.STATUS_FAILED);
            paymentRepository.save(payment);

            eventPublisher.publishFailed(PaymentFailedEvent.builder()
                    .paymentId(payment.getPaymentId())
                    .orderId(payment.getOrderId())
                    .reason(VnpayConstants.FAILED_REASON_PREFIX + responseCode)
                    .occurredAt(Instant.now())
                    .build());
        }

        return toResponse(payment);
    }

    @Transactional
    public PaymentResponse confirmCod(Long paymentId) {
        Payment payment = findPaymentById(paymentId);

        if (!Objects.equals(payment.getPaymentMethod(), PaymentMethod.CASH)) {
            throw new BusinessException(
                    ErrorCode.VALIDATION_ERROR,
                    MessageHandlerUtils.getMessage(CommonMessageUtils.Payment.ONLY_CASH_CONFIRM));
        }
        if (Objects.equals(payment.getPaymentStatus(), PaymentStatus.PAID)) {
            return toResponse(payment);
        }

        payment.setPaymentStatus(PaymentStatus.PAID);
        payment.setStatus(VnpayConstants.STATUS_SUCCESS);
        payment.setPaymentDate(LocalDateTime.now());
        paymentRepository.save(payment);

        eventPublisher.publishSucceeded(PaymentSucceededEvent.builder()
                .paymentId(payment.getPaymentId())
                .orderId(payment.getOrderId())
                .paymentMethod(PaymentMethod.CASH)
                .amount(payment.getAmount())
                .transactionNo(null)
                .occurredAt(Instant.now())
                .build());

        return toResponse(payment);
    }

    @Transactional(readOnly = true)
    public PaymentResponse getById(Long paymentId) {
        return toResponse(findPaymentById(paymentId));
    }

    @Transactional(readOnly = true)
    public PaymentResponse getByOrderId(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        MessageHandlerUtils.getMessage(CommonMessageUtils.Payment.NOT_FOUND)));
        return toResponse(payment);
    }

    @Scheduled(fixedDelay = 60_000)
    @Transactional
    public void checkExpiredPayments() {
        log.debug("Checking for expired payments...");
        LocalDateTime now = LocalDateTime.now();

        List<Payment> expired = paymentRepository.findByPaymentStatusInAndExpiredAtBefore(
                List.of(PaymentStatus.INIT, PaymentStatus.UNPAID), now);

        if (expired.isEmpty()) return;

        for (Payment payment : expired) {
            if (Objects.equals(payment.getPaymentStatus(), PaymentStatus.EXPIRED)) continue;

            payment.setPaymentStatus(PaymentStatus.EXPIRED);
            payment.setStatus(VnpayConstants.STATUS_EXPIRED);

            eventPublisher.publishExpired(PaymentExpiredEvent.builder()
                    .paymentId(payment.getPaymentId())
                    .orderId(payment.getOrderId())
                    .occurredAt(Instant.now())
                    .build());

            log.info("Payment {} for order {} marked EXPIRED",
                    payment.getPaymentId(), payment.getOrderId());
        }

        paymentRepository.saveAll(expired);
    }

    private Payment findPaymentById(Long paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        MessageHandlerUtils.getMessage(CommonMessageUtils.Payment.NOT_FOUND)));
    }

    private PaymentResponse toResponse(Payment p) {
        return PaymentResponse.builder()
                .paymentId(p.getPaymentId())
                .orderId(p.getOrderId())
                .paymentMethod(p.getPaymentMethod())
                .paymentStatus(p.getPaymentStatus())
                .vnpTxnRef(p.getVnpTxnRef())
                .amount(p.getAmount())
                .orderInfo(p.getOrderInfo())
                .bankCode(p.getBankCode())
                .responseCode(p.getResponseCode())
                .paymentDate(p.getPaymentDate())
                .transactionNo(p.getTransactionNo())
                .cardType(p.getCardType())
                .build();
    }
}
