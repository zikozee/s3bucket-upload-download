package com.phyna.uploaddocument.domain;

import com.phyna.uploaddocument.exceptions.ProcessException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author : Ezekiel Eromosei
 * @created : 26 Sep, 2021
 */

public interface S3BucketService {

    void uploadFile(String uniqueId, MultipartFile file) throws ProcessException;

    byte[] downloadFile(String uniqueId, String filename) throws ProcessException;

    void uploadBase64File(UploadBean uploadBean) throws ProcessException;
}
