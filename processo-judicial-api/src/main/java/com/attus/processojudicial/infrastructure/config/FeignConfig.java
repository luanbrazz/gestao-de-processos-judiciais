package com.attus.processojudicial.infrastructure.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "com.attus.processojudicial.infrastructure.client")
public class FeignConfig {
}