package com.xdream.file.controller;

import com.xdream.common.dto.ApiResponse;
import com.xdream.common.dto.PageResponse;
import com.xdream.file.dto.*;
import com.xdream.file.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Tag(name = "文件管理", description = "文件上传下载管理相关接口")
@SecurityRequirement(name = "Bearer Authentication")
public class FileController {
    
    private final FileService fileService;
    
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "上传文件", description = "上传单个文件")
    public ResponseEntity<ApiResponse<FileUploadResponse>> uploadFile(
            @RequestHeader("X-User-Id") String userId,
            @Parameter(description = "文件") @RequestParam("file") MultipartFile file,
            @Parameter(description = "文件类型") @RequestParam(required = false) String fileType,
            @Parameter(description = "文件描述") @RequestParam(required = false) String description) {
        FileUploadRequest request = new FileUploadRequest();
        request.setFile(file);
        request.setFileType(fileType);
        request.setDescription(description);
        
        FileUploadResponse response = fileService.uploadFile(userId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @PostMapping(value = "/upload/batch", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "批量上传文件", description = "上传多个文件")
    public ResponseEntity<ApiResponse<BatchUploadResponse>> uploadFiles(
            @RequestHeader("X-User-Id") String userId,
            @Parameter(description = "文件列表") @RequestParam("files") MultipartFile[] files,
            @Parameter(description = "文件类型") @RequestParam(required = false) String fileType,
            @Parameter(description = "文件描述") @RequestParam(required = false) String description) {
        BatchUploadRequest request = new BatchUploadRequest();
        request.setFiles(files);
        request.setFileType(fileType);
        request.setDescription(description);
        
        BatchUploadResponse response = fileService.uploadFiles(userId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/{fileId}")
    @Operation(summary = "获取文件信息", description = "获取文件详细信息")
    public ResponseEntity<ApiResponse<FileInfoResponse>> getFileInfo(
            @RequestHeader("X-User-Id") String userId,
            @Parameter(description = "文件ID") @PathVariable String fileId) {
        FileInfoResponse response = fileService.getFileInfo(userId, fileId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/{fileId}/download")
    @Operation(summary = "下载文件", description = "下载指定文件")
    public ResponseEntity<Resource> downloadFile(
            @RequestHeader("X-User-Id") String userId,
            @Parameter(description = "文件ID") @PathVariable String fileId) {
        FileDownloadResponse response = fileService.downloadFile(userId, fileId);
        
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(response.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + response.getFileName() + "\"")
                .body(response.getResource());
    }
    
    @GetMapping("/{fileId}/preview")
    @Operation(summary = "预览文件", description = "在线预览文件")
    public ResponseEntity<Resource> previewFile(
            @RequestHeader("X-User-Id") String userId,
            @Parameter(description = "文件ID") @PathVariable String fileId) {
        FileDownloadResponse response = fileService.downloadFile(userId, fileId);
        
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(response.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + response.getFileName() + "\"")
                .body(response.getResource());
    }
    
    @GetMapping
    @Operation(summary = "获取文件列表", description = "获取用户文件列表")
    public ResponseEntity<ApiResponse<PageResponse<FileInfoResponse>>> getFiles(
            @RequestHeader("X-User-Id") String userId,
            @RequestParam(required = false) String fileType,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 20) Pageable pageable) {
        PageResponse<FileInfoResponse> response = fileService.getFiles(userId, fileType, keyword, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @DeleteMapping("/{fileId}")
    @Operation(summary = "删除文件", description = "删除指定文件")
    public ResponseEntity<ApiResponse<Void>> deleteFile(
            @RequestHeader("X-User-Id") String userId,
            @Parameter(description = "文件ID") @PathVariable String fileId) {
        fileService.deleteFile(userId, fileId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
    
    @GetMapping("/stats")
    @Operation(summary = "获取文件统计", description = "获取用户文件统计信息")
    public ResponseEntity<ApiResponse<FileStatsResponse>> getFileStats(
            @RequestHeader("X-User-Id") String userId) {
        FileStatsResponse response = fileService.getFileStats(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @PostMapping("/{fileId}/share")
    @Operation(summary = "分享文件", description = "创建文件分享链接")
    public ResponseEntity<ApiResponse<FileShareResponse>> shareFile(
            @RequestHeader("X-User-Id") String userId,
            @Parameter(description = "文件ID") @PathVariable String fileId,
            @Valid @RequestBody FileShareRequest request) {
        FileShareResponse response = fileService.shareFile(userId, fileId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @GetMapping("/share/{shareId}")
    @Operation(summary = "获取分享文件", description = "通过分享链接获取文件信息")
    public ResponseEntity<ApiResponse<FileInfoResponse>> getSharedFile(
            @Parameter(description = "分享ID") @PathVariable String shareId) {
        FileInfoResponse response = fileService.getSharedFile(shareId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}