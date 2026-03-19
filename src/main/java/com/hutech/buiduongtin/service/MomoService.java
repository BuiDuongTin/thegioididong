package com.hutech.buiduongtin.service;

import com.hutech.buiduongtin.config.MomoConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.web.client.RestClientResponseException;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MomoService {
    private final MomoConfig momoConfig;

    public String createPayment(String dbOrderId, double amount, String orderInfo) throws Exception {
        String requestId = momoConfig.getPartnerCode() + System.currentTimeMillis();
        String momoOrderId = requestId + "x" + dbOrderId;
        String amountStr = String.valueOf((long) amount);

        String requestType = "captureWallet";
        String extraData = "";

        // HMAC SHA256 signature generation - MUST BE ALPHABETICAL
        String rawData = "accessKey=" + momoConfig.getAccessKey() +
                "&amount=" + amountStr +
                "&extraData=" + extraData +
                "&ipnUrl=" + momoConfig.getNotifyUrl() +
                "&orderId=" + momoOrderId +
                "&orderInfo=" + orderInfo +
                "&partnerCode=" + momoConfig.getPartnerCode() +
                "&redirectUrl=" + momoConfig.getReturnUrl() +
                "&requestId=" + requestId +
                "&requestType=" + requestType;

        String signature = signHmacSHA256(rawData, momoConfig.getSecretKey());

        // Prepare JSON payload
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("partnerCode", momoConfig.getPartnerCode());
        requestBody.put("accessKey", momoConfig.getAccessKey());
        requestBody.put("requestId", requestId);
        requestBody.put("amount", amountStr);
        requestBody.put("orderId", momoOrderId);
        requestBody.put("orderInfo", orderInfo);
        requestBody.put("redirectUrl", momoConfig.getReturnUrl());
        requestBody.put("ipnUrl", momoConfig.getNotifyUrl());
        requestBody.put("extraData", extraData);
        requestBody.put("requestType", requestType);
        requestBody.put("signature", signature);
        requestBody.put("lang", "en");

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.postForObject(momoConfig.getEndpointUrl(), entity, Map.class);

            if (response != null && response.containsKey("payUrl")) {
                return response.get("payUrl").toString();
            }
        } catch (RestClientResponseException e) {
            System.err.println("MOMO API REQUEST FAILED!");
            System.err.println("HTTP Status: " + e.getRawStatusCode());
            System.err.println("Response Body: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            System.err.println("Unknown Error during Momo API execution: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    private String signHmacSHA256(String data, String secretKey) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        byte[] hash = sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1)
                hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
