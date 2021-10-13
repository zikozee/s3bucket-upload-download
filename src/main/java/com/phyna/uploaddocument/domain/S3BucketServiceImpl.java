package com.phyna.uploaddocument.domain;

import com.phyna.uploaddocument.bucket.BucketName;
import com.phyna.uploaddocument.exceptions.ProcessException;
import com.phyna.uploaddocument.filestore.FileStoreService;
import com.phyna.uploaddocument.util.Utility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author : Ezekiel Eromosei
 * @created : 26 Sep, 2021
 */

@Service
@RequiredArgsConstructor
public class S3BucketServiceImpl implements S3BucketService{
    public static final String SEPARATOR = "/";

    private final FileStoreService fileStoreService;

    @Override
    public void uploadFile(String uniqueId, MultipartFile file) throws ProcessException {
        //1. Check if Image is not empty
        isFileEmpty(file);

        //2. Grab some metadata from file if any
        Map<String, String> metadata = extractMetadata(file);

        //5. Store the image in s3 and update database (userProfileImageLink) with s3 image link
        //creating a folder per user
        String path = BucketName.PROFILE_IMAGE.getBucketName() + SEPARATOR + uniqueId;
        String fileName = uniqueId + "-" + file.getOriginalFilename();
        try {
            fileStoreService.save(uniqueId, path, fileName, Optional.of(metadata), file.getInputStream(), file.getSize());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void uploadBase64File(UploadBean uploadBean) throws ProcessException {

        File file = Utility.getImageFromBase64(uploadBean.getImage(), uploadBean.getName());

        Map<String, String> metadata = extractMetadata(file);
        String path = BucketName.PROFILE_IMAGE.getBucketName() + SEPARATOR + uploadBean.getUniqueId();
        String fileName = uploadBean.getUniqueId() + "-" + uploadBean.getName();
        fileStoreService.save(uploadBean.getUniqueId(), path, fileName, Optional.of(metadata), file);
    }


    @Override
    public byte[] downloadFile(String uniqueId, String filename) throws ProcessException {
        String path = BucketName.PROFILE_IMAGE.getBucketName() + SEPARATOR + uniqueId;

        try {
            return fileStoreService.download(path, filename);
        } catch (ProcessException e) {
            throw new ProcessException("Kindly ensure Unique Id: '" + uniqueId + "' and '" + filename + "' is correct");
        }
    }



    private Map<String, String> extractMetadata(MultipartFile file) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("Content-Type", file.getContentType());
        metadata.put("Content-Length", String.valueOf(file.getSize()));
        return metadata;
    }

    private Map<String, String> extractMetadata(File file) throws  ProcessException {
        Map<String, String> metadata = new HashMap<>();
        URLConnection connection;
        try {
            connection = file.toURI().toURL().openConnection();
        }catch (IOException ex){
            throw new ProcessException(ex.getLocalizedMessage());
        }

        metadata.put("Content-Type", connection.getContentType());
        metadata.put("Content-Length", String.valueOf(file.length()));
        return metadata;
    }

    private void isFileEmpty(MultipartFile file) throws ProcessException {
        if(file.isEmpty())
            throw new ProcessException("Cannot upload empty file [ " + file.getSize() + "]");//throw custom exception
    }
}
