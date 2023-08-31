package org.upsmf.grievance.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class FileUploadRequest {

    @Getter
    private MultipartFile file;
    private Long uploadedBy;

}
