package com.hutech.buiduongtin.config;

import org.springframework.context.annotation.Configuration;
import lombok.Getter;

@Configuration
@Getter
public class MomoConfig {
    // Basic test keys often used for MoMo Sandbox, or user can replace with their
    // own.
    private final String partnerCode = "MOMO";
    private final String accessKey = "F8BBA842ECF85"; // Dummy Sandbox keys
    private final String secretKey = "K951B6PE1waDMi640xX08PD3vg6EkVlz";

    // Gateway endpoint for POST
    private final String endpointUrl = "https://test-payment.momo.vn/v2/gateway/api/create";

    private final String returnUrl = "http://localhost:8080/momo/return";
    private final String notifyUrl = "http://localhost:8080/momo/notify"; // NGROK might be needed for real IPN, but
                                                                          // we'll handle standard logic.
}
