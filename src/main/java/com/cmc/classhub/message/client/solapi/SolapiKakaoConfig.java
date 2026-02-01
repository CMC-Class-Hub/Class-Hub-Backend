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
@ConfigurationProperties(prefix = "message.kakao.solapi")
@ConditionalOnProperty(prefix = "message.sms", name = "provider", havingValue = "kakao")
@Getter
@Setter
public class SolapiKakaoConfig {

    private String apiKey;
    private String apiSecret;
    private String from;
    private String pfId;  // 카카오톡 채널 발신프로필 ID

    @Bean
    public DefaultMessageService solapiKakaoMessageService() {
        return SolapiClient.INSTANCE.createInstance(apiKey, apiSecret);
    }
}
