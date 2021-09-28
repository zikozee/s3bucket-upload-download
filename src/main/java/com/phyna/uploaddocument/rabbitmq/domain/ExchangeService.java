package com.phyna.uploaddocument.rabbitmq.domain;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * @author : Ezekiel Eromosei
 * @created : 27 Sep, 2021
 */

public interface ExchangeService {

    void fanoutSendMessage(UploadResponse response);
}
