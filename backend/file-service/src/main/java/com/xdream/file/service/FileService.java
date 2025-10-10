package com.xdream.file.service;

import com.xdream.common.dto.PageResponse;
import com.xdream.file.dto.*;
import org.springframework.data.domain.Pageable;

public interface FileService {
    
    FileUploadResponse uploadFile(String userId, FileUploadRequest request);
    
    BatchUploadResponse uploadFiles(String userId, BatchUploadRequest request);
    
    FileInfoResponse getFileInfo(String userId, String fileId);
    
    FileDownloadResponse downloadFile(String userId, String fileId);
    
    PageResponse<FileInfoResponse> getFiles(String userId, String fileType, String keyword, Pageable pageable);
    
    void deleteFile(String userId, String fileId);
    
    FileStatsResponse getFileStats(String userId);
    
    FileShareResponse shareFile(String userId, String fileId, FileShareRequest request);
    
    FileInfoResponse getSharedFile(String shareId);
}