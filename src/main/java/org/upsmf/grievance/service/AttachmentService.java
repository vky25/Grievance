package org.upsmf.grievance.service;

import org.upsmf.grievance.dto.FileUploadRequest;

import java.io.IOException;

public interface AttachmentService {

    void uploadObject(FileUploadRequest fileUploadRequest);
}
