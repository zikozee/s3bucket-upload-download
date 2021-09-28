package com.phyna.uploaddocument.domain;

import com.phyna.uploaddocument.exceptions.ProcessException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author : Ezekiel Eromosei
 * @created : 26 Sep, 2021
 */

@RestController
@RequestMapping("/api/v1/s3Bucket")
@RequiredArgsConstructor
public class S3BucketController {

    private final S3BucketService s3BucketService;

    @PostMapping(path = "file/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void uploadFile(@RequestParam("uniqueId") String uniqueId,
                           @RequestParam("file") MultipartFile file) throws ProcessException {
        s3BucketService.uploadFile(uniqueId, file);
    }

    @GetMapping(path = "file/download")
    public byte[] downloadFile(@RequestParam("uniqueId") String uniqueId,
                                           @RequestParam("filename") String filename) throws ProcessException {
        return s3BucketService.downloadFile(uniqueId, filename);
    }
}
