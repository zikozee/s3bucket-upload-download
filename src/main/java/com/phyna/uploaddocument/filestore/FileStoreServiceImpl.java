package com.phyna.uploaddocument.filestore;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressEventType;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.util.IOUtils;
import com.phyna.uploaddocument.exceptions.ProcessException;
import com.phyna.uploaddocument.rabbitmq.domain.ExchangeService;
import com.phyna.uploaddocument.rabbitmq.domain.UploadResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

/**
 * @author : Ezekiel Eromosei
 * @created : 26 Sep, 2021
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class FileStoreServiceImpl implements FileStoreService{

    private final AmazonS3 s3;
    private final ExchangeService exchangeService;

    @Override
    public void save(String uniqueId, String path, String fileName, Optional<Map<String, String>> optionalMetadata, InputStream inputStream, long contentLength) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(contentLength);
        optionalMetadata.ifPresent(map -> {
            if(!map.isEmpty()){
                map.forEach(metadata::addUserMetadata);
            }
        });

        try{
            TransferManager tm = new TransferManager(s3);
            PutObjectRequest request = new PutObjectRequest(path, fileName, inputStream, metadata);

            request.setGeneralProgressListener(new ProgressListener() {
                @Override
                public void progressChanged(ProgressEvent progressEvent) {
                    final ProgressEventType progressEventType = progressEvent.getEventType();
                    //log.info("Transferred bytes: " + progressEvent.getBytesTransferred());

                    if(progressEventType == ProgressEventType.TRANSFER_COMPLETED_EVENT){
                        log.info("document transfer completed successfully");
                        UploadResponse uploadResponse = UploadResponse.builder()
                                .message("success").filename(fileName).uniqueId(uniqueId).build();
                        exchangeService.fanoutSendMessage(uploadResponse);
                    }
                }
            });


            tm.upload(request);
        } catch (AmazonServiceException e){
            throw new IllegalStateException("Failed to store file to s3", e);
        }
    }

    @Override
    public byte[] download(String path, String key) throws ProcessException {
        try{
            S3Object object = s3.getObject(path, key);
            S3ObjectInputStream inputStream = object.getObjectContent();
            return IOUtils.toByteArray(inputStream);
        }catch (AmazonServiceException| IOException e){
            throw new ProcessException("Failed to download file to s3: " +  e.getLocalizedMessage());
        }
    }
}
