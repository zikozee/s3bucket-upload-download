package com.phyna.uploaddocument.filestore;

import com.phyna.uploaddocument.exceptions.ProcessException;

import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

public interface FileStoreService {

    void save(String uniqueId, String path, String fileName, Optional<Map<String, String>> optionalMetadata, InputStream inputStream, long contentLength);

    byte[] download(String path, String key) throws ProcessException;
}
