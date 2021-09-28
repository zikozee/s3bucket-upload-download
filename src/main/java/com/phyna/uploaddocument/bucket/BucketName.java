package com.phyna.uploaddocument.bucket;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BucketName {

    PROFILE_IMAGE("phyna-upload");

    private final String bucketName;

}
