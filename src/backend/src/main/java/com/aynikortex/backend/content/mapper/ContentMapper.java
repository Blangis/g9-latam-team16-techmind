package com.aynikortex.backend.content.mapper;

import com.aynikortex.backend.content.dto.ContentResponseDTO;
import com.aynikortex.backend.content.entity.Content;
import org.springframework.stereotype.Component;

@Component
public class ContentMapper {

    public Content toEntity(ContentRequestDTO requestDTO) {
        Content content = new Content();
        content.setTitle(requestDTO.title());
        content.setContentType(requestDTO.contentType());
        content.setTextContent(requestDTO.textContent());
        content.setFileName(requestDTO.fileName());
        content.setFilePath(requestDTO.filePath());
        return content;
    }

    public ContentResponseDTO toResponseDTO(Content content) {
        return new ContentResponseDTO(
                content.getId(),
                content.getTitle(),
                content.getContentType(),
                content.getCategory(),
                content.getCreatedAt()
        );
    }
}