package com.cmc.classhub.message.client.solapi;

import com.solapi.sdk.SolapiClient;
import com.solapi.sdk.message.service.DefaultMessageService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "message.sms.solapi")
@ConditionalOnProperty(prefix = "message.sms", name = "provider", havingValue = "solapi")
@Getter
@Setter
public class SolapiConfig {

    private String apiKey;
    private String apiSecret;
    private String from;

    @Bean
    public DefaultMessageService solapiMessageService() {
        return SolapiClient.INSTANCE.createInstance(apiKey, apiSecret);
    }
}
