package com.phyna.uploaddocument.rabbitmq.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

/**
 * @author : Ezekiel Eromosei
 * @created : 27 Sep, 2021
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class ExchangeServiceImpl implements ExchangeService {

    public static final String EXCHANGE_NAME = "phyna.upload";
    public static final String ROUTING_KEY = ""; //since fanout will send to all queues, hence no effect
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void fanoutSendMessage(UploadResponse response) {
        try {
            String json = objectMapper.writeValueAsString(response);
            rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, json);
        } catch (JsonProcessingException e) {
            log.error("Error Parsing: {}", e.getMessage());
        }

    }
}
