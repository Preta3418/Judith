package com.judtih.judith_management_system.shared.upload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadResponse {

    String url;
    Long size;
    String folder;
    LocalDateTime uploadTime;
}
