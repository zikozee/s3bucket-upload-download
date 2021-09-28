package com.phyna.uploaddocument.rabbitmq.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author : zikoz
 * @created : 04 Jun, 2021
 */

@Configuration
public class RabbitmqConfig {

    @Bean
    public ObjectMapper objectMapper(){
        return JsonMapper.builder().findAndAddModules().build();
    }
}
