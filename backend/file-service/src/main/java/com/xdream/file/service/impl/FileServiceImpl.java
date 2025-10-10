package com.xdream.file.service.impl;

import com.xdream.common.dto.PageResponse;
import com.xdream.file.dto.*;
import com.xdream.file.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileServiceImpl implements FileService {
    
    // 模拟文件存储
    private final Map<String, FileInfoResponse> files = new ConcurrentHashMap<>();
    private final Map<String, byte[]> fileContents = new ConcurrentHashMap<>();
    private final Map<String, FileShareResponse> shares = new ConcurrentHashMap<>();
    
    @Override
    public FileUploadResponse uploadFile(String userId, FileUploadRequest request) {
        MultipartFile file = request.getFile();
        
        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }
        
        try {
            String fileId = UUID.randomUUID().toString();
            String originalFilename = file.getOriginalFilename();
            String fileType = getFileType(originalFilename);
            String mimeType = file.getContentType();
            byte[] content = file.getBytes();
            String md5 = calculateMD5(content);
            
            // 存储文件信息
            FileInfoResponse fileInfo = new FileInfoResponse();
            fileInfo.setFileId(fileId);
            fileInfo.setFileName(originalFilename);
            fileInfo.setOriginalName(originalFilename);
            fileInfo.setFileSize(file.getSize());
            fileInfo.setFileType(fileType);
            fileInfo.setMimeType(mimeType);
            fileInfo.setFileUrl("/api/files/" + fileId + "/download");
            fileInfo.setMd5(md5);
            fileInfo.setDescription(request.getDescription());
            fileInfo.setUploadedBy(userId);
            fileInfo.setUploadTime(LocalDateTime.now());
            fileInfo.setDownloadCount(0);
            fileInfo.setStatus("active");
            
            files.put(fileId, fileInfo);
            fileContents.put(fileId, content);
            
            FileUploadResponse response = new FileUploadResponse();
            response.setFileId(fileId);
            response.setFileName(originalFilename);
            response.setFileSize(file.getSize());
            response.setFileType(fileType);
            response.setMimeType(mimeType);
            response.setFileUrl(fileInfo.getFileUrl());
            response.setMd5(md5);
            response.setUploadTime(LocalDateTime.now());
            
            log.info("File uploaded: {} by user: {} ({} bytes)", fileId, userId, file.getSize());
            return response;
            
        } catch (IOException e) {
            log.error("File upload failed for user: {}", userId, e);
            throw new RuntimeException("File upload failed", e);
        }
    }
    
    @Override
    public BatchUploadResponse uploadFiles(String userId, BatchUploadRequest request) {
        long startTime = System.currentTimeMillis();
        List<FileUploadResponse> successfulFiles = new ArrayList<>();
        List<BatchUploadResponse.FileErrorResponse> failedFiles = new ArrayList<>();
        
        for (MultipartFile file : request.getFiles()) {
            try {
                FileUploadRequest singleRequest = new FileUploadRequest();
                singleRequest.setFile(file);
                singleRequest.setFileType(request.getFileType());
                singleRequest.setDescription(request.getDescription());
                
                FileUploadResponse response = uploadFile(userId, singleRequest);
                successfulFiles.add(response);
                
            } catch (Exception e) {
                BatchUploadResponse.FileErrorResponse error = new BatchUploadResponse.FileErrorResponse();
                error.setFileName(file.getOriginalFilename());
                error.setErrorMessage(e.getMessage());
                failedFiles.add(error);
                
                log.error("File upload failed in batch: {} by user: {}", file.getOriginalFilename(), userId, e);
            }
        }
        
        BatchUploadResponse response = new BatchUploadResponse();
        response.setSuccessfulFiles(successfulFiles);
        response.setFailedFiles(failedFiles);
        response.setTotalFiles(request.getFiles().length);
        response.setSuccessCount(successfulFiles.size());
        response.setFailCount(failedFiles.size());
        response.setDuration(System.currentTimeMillis() - startTime);
        
        log.info("Batch upload completed for user: {} - {} success, {} failed", 
                userId, successfulFiles.size(), failedFiles.size());
        return response;
    }
    
    @Override
    public FileInfoResponse getFileInfo(String userId, String fileId) {
        FileInfoResponse file = files.get(fileId);
        if (file == null || !file.getUploadedBy().equals(userId)) {
            throw new RuntimeException("File not found or access denied");
        }
        return file;
    }
    
    @Override
    public FileDownloadResponse downloadFile(String userId, String fileId) {
        FileInfoResponse file = getFileInfo(userId, fileId);
        byte[] content = fileContents.get(fileId);
        
        if (content == null) {
            throw new RuntimeException("File content not found");
        }
        
        // 更新下载次数
        file.setDownloadCount(file.getDownloadCount() + 1);
        
        Resource resource = new ByteArrayResource(content);
        
        FileDownloadResponse response = new FileDownloadResponse();
        response.setResource(resource);
        response.setFileName(file.getFileName());
        response.setContentType(file.getMimeType());
        response.setFileSize(file.getFileSize());
        
        log.info("File downloaded: {} by user: {} ({} bytes)", fileId, userId, file.getFileSize());
        return response;
    }
    
    @Override
    public PageResponse<FileInfoResponse> getFiles(String userId, String fileType, String keyword, Pageable pageable) {
        List<FileInfoResponse> filteredFiles = files.values().stream()
                .filter(file -> file.getUploadedBy().equals(userId))
                .filter(file -> fileType == null || fileType.equals(file.getFileType()))
                .filter(file -> keyword == null || 
                        (file.getFileName() != null && file.getFileName().toLowerCase().contains(keyword.toLowerCase())) ||
                        (file.getDescription() != null && file.getDescription().toLowerCase().contains(keyword.toLowerCase())))
                .sorted(Comparator.comparing(FileInfoResponse::getUploadTime).reversed())
                .collect(Collectors.toList());
        
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), filteredFiles.size());
        
        List<FileInfoResponse> pageContent = start >= filteredFiles.size() ? 
                Collections.emptyList() : filteredFiles.subList(start, end);
        
        return PageResponse.of(pageContent, filteredFiles.size(), pageable.getPageNumber(), pageable.getPageSize());
    }
    
    @Override
    public void deleteFile(String userId, String fileId) {
        FileInfoResponse file = getFileInfo(userId, fileId);
        
        files.remove(fileId);
        fileContents.remove(fileId);
        
        // 删除相关的分享
        shares.entrySet().removeIf(entry -> entry.getValue().getShareUrl().contains(fileId));
        
        log.info("File deleted: {} by user: {}", fileId, userId);
    }
    
    @Override
    public FileStatsResponse getFileStats(String userId) {
        List<FileInfoResponse> userFiles = files.values().stream()
                .filter(file -> file.getUploadedBy().equals(userId))
                .collect(Collectors.toList());
        
        long totalFiles = userFiles.size();
        long totalSize = userFiles.stream()
                .mapToLong(FileInfoResponse::getFileSize)
                .sum();
        
        Map<String, Long> fileTypeStats = userFiles.stream()
                .collect(Collectors.groupingBy(
                        file -> file.getFileType() != null ? file.getFileType() : "unknown",
                        Collectors.counting()
                ));
        
        Map<String, Long> sizeDistribution = calculateSizeDistribution(userFiles);
        
        // 模拟最近7天上传统计
        Map<String, Long> weeklyUploadTrend = new LinkedHashMap<>();
        LocalDate today = LocalDate.now();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            String dateStr = date.toString();
            long count = (long) (Math.random() * 5); // 模拟数据
            weeklyUploadTrend.put(dateStr, count);
        }
        
        // 本月统计
        LocalDateTime firstDayOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        long monthlyUploadCount = userFiles.stream()
                .filter(file -> file.getUploadTime().isAfter(firstDayOfMonth))
                .count();
        
        long monthlyUploadSize = userFiles.stream()
                .filter(file -> file.getUploadTime().isAfter(firstDayOfMonth))
                .mapToLong(FileInfoResponse::getFileSize)
                .sum();
        
        LocalDateTime lastUploadTime = userFiles.stream()
                .map(FileInfoResponse::getUploadTime)
                .max(LocalDateTime::compareTo)
                .orElse(null);
        
        FileStatsResponse stats = new FileStatsResponse();
        stats.setTotalFiles(totalFiles);
        stats.setTotalSize(totalSize);
        stats.setFileTypeStats(fileTypeStats);
        stats.setSizeDistribution(sizeDistribution);
        stats.setWeeklyUploadTrend(weeklyUploadTrend);
        stats.setMonthlyUploadCount(monthlyUploadCount);
        stats.setMonthlyUploadSize(monthlyUploadSize);
        stats.setLastUploadTime(lastUploadTime);
        
        return stats;
    }
    
    @Override
    public FileShareResponse shareFile(String userId, String fileId, FileShareRequest request) {
        FileInfoResponse file = getFileInfo(userId, fileId);
        
        String shareId = UUID.randomUUID().toString();
        LocalDateTime expireTime = LocalDateTime.now().plusHours(request.getExpireHours());
        
        FileShareResponse share = new FileShareResponse();
        share.setShareId(shareId);
        share.setShareUrl("/api/files/share/" + shareId);
        share.setPassword(request.getPassword());
        share.setExpireTime(expireTime);
        share.setMaxDownloads(request.getMaxDownloads());
        share.setDownloadCount(0);
        share.setCreatedAt(LocalDateTime.now());
        
        shares.put(shareId, share);
        
        log.info("File shared: {} by user: {} with shareId: {}", fileId, userId, shareId);
        return share;
    }
    
    @Override
    public FileInfoResponse getSharedFile(String shareId) {
        FileShareResponse share = shares.get(shareId);
        if (share == null) {
            throw new RuntimeException("Share not found");
        }
        
        // 检查是否过期
        if (share.getExpireTime().isBefore(LocalDateTime.now())) {
            shares.remove(shareId);
            throw new RuntimeException("Share has expired");
        }
        
        // 检查下载次数限制
        if (share.getDownloadCount() >= share.getMaxDownloads()) {
            throw new RuntimeException("Download limit exceeded");
        }
        
        // 增加下载次数
        share.setDownloadCount(share.getDownloadCount() + 1);
        
        // 这里需要根据分享链接找到对应的文件
        // 简化实现：假设分享URL包含文件ID
        String fileId = extractFileIdFromShareUrl(share.getShareUrl());
        FileInfoResponse file = files.get(fileId);
        
        if (file == null) {
            throw new RuntimeException("File not found");
        }
        
        log.info("Shared file accessed: {} with shareId: {}", fileId, shareId);
        return file;
    }
    
    private String getFileType(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "unknown";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }
    
    private String calculateMD5(byte[] content) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(content);
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            return "";
        }
    }
    
    private Map<String, Long> calculateSizeDistribution(List<FileInfoResponse> files) {
        Map<String, Long> distribution = new LinkedHashMap<>();
        distribution.put("0-1MB", 0L);
        distribution.put("1-10MB", 0L);
        distribution.put("10-100MB", 0L);
        distribution.put("100MB+", 0L);
        
        for (FileInfoResponse file : files) {
            long sizeInMB = file.getFileSize() / (1024 * 1024);
            if (sizeInMB < 1) {
                distribution.put("0-1MB", distribution.get("0-1MB") + 1);
            } else if (sizeInMB < 10) {
                distribution.put("1-10MB", distribution.get("1-10MB") + 1);
            } else if (sizeInMB < 100) {
                distribution.put("10-100MB", distribution.get("10-100MB") + 1);
            } else {
                distribution.put("100MB+", distribution.get("100MB+") + 1);
            }
        }
        
        return distribution;
    }
    
    private String extractFileIdFromShareUrl(String shareUrl) {
        // 简化实现：从URL中提取文件ID
        // 实际应用中需要更复杂的逻辑
        String[] parts = shareUrl.split("/");
        if (parts.length >= 2) {
            return parts[parts.length - 2];
        }
        return "";
    }
}