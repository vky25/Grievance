package org.upsmf.grievance.dto;

import lombok.*;

import java.io.File;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class FileUploadRequest {

    private File file;
    private String fileName;
    private String fileExtension;
    private Timestamp createdDate;
    private Timestamp updatedDate;
    private Long uploadedBy;
}
