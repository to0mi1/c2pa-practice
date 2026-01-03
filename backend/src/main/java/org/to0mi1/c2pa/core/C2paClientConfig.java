package org.to0mi1.c2pa.core;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class C2paClientConfig {

    @Value("${c2pa.core-server.url}")
    private String coreServerUrl;

    @Bean(name = "c2paRestClient")
    public RestClient c2paRestClient() {
        return RestClient.builder()
//                .requestFactory(new HttpComponentsClientHttpRequestFactory())
                .baseUrl(coreServerUrl)
                .build();
    }
}
