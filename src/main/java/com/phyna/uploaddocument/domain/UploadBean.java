package com.phyna.uploaddocument.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : Ezekiel Eromosei
 * @created : 13 Oct, 2021
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UploadBean {
    private String image;
    private String name;
    private String uniqueId;
}
