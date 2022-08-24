package ru.turaev.gateway.config;

import lombok.RequiredArgsConstructor;
import org.springdoc.core.AbstractSwaggerUiConfigProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

@RestController
@RequiredArgsConstructor
public class SwaggerController {
    private final DiscoveryClient discoveryClient;

    @Value("${spring.application.name}")
    private String gatewayServerName;

    @GetMapping("/v3/api-docs/swagger-config")
    public Map<String, Object> swaggerConfig(ServerHttpRequest serverHttpRequest) throws URISyntaxException {
        URI uri = serverHttpRequest.getURI();
        String url = new URI(uri.getScheme(), uri.getAuthority(), null, null, null).toString();
        Map<String, Object> swaggerConfig = new LinkedHashMap<>();
        List<AbstractSwaggerUiConfigProperties.SwaggerUrl> swaggerUrls = new LinkedList<>();
        for (String serviceName : discoveryClient.getServices()) {
            swaggerUrls.add(new AbstractSwaggerUiConfigProperties.SwaggerUrl(serviceName, url + "/" + serviceName + "/v2/api-docs"));
        }
        swaggerUrls.removeIf(swaggerUrl -> swaggerUrl.getName().equals(gatewayServerName));
        swaggerConfig.put("urls", swaggerUrls);
        return swaggerConfig;
    }
}