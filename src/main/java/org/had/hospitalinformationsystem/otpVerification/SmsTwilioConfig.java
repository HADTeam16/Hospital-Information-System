package org.had.hospitalinformationsystem.otpVerification;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@Component
@ConfigurationProperties(prefix = "twilio")
@Data
public class SmsTwilioConfig {
    private String accountSid;
    private String authToken;
    private String trialNumber;
}
