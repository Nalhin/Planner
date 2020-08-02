package com.memes.upload;

import com.memes.upload.exceptions.ImageNotSavedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileUploadService {

  @Value("${image-upload-url}")
  private String fileUploadUrl;

  @Value("${image-access-url}")
  private String imageUrl;

  private final RestTemplate restTemplate;

  public FileUploadService(RestTemplateBuilder restTemplateBuilder) {
    this.restTemplate = restTemplateBuilder.build();
  }

  public String uploadFile(MultipartFile file, String fileName) throws ImageNotSavedException {
    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
    try {
      ByteArrayResource contentsAsResource =
          new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
              return fileName;
            }
          };
      body.add("image", contentsAsResource);
    } catch (Exception ignored) {
    }

    HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body);

    ResponseEntity<String> response =
        restTemplate.postForEntity(fileUploadUrl, requestEntity, String.class);

    if (!response.getStatusCode().is2xxSuccessful()) {
      throw new ImageNotSavedException();
    }

    return imageUrl + fileName;
  }
}
