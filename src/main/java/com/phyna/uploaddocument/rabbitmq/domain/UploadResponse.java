package com.phyna.uploaddocument.rabbitmq.domain;

import lombok.*;

/**
 * @author : Ezekiel Eromosei
 * @created : 27 Sep, 2021
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class UploadResponse {
    private String message;
    private String uniqueId;
    private String filename;
    private String downloadUrl;
}
