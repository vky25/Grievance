package org.upsmf.grievance.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface AttachmentService {

    ResponseEntity<String> uploadObject(MultipartFile file);
}
