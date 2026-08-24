package com.aynikortex.backend.content.mapper;

import com.aynikortex.backend.content.dto.request.CreateFileContentRequest;
import com.aynikortex.backend.content.dto.request.CreateTextContentRequest;
import com.aynikortex.backend.content.dto.response.ContentResponseDTO;
import com.aynikortex.backend.content.dto.response.KeywordDTO;
import com.aynikortex.backend.content.entity.Content;
import com.aynikortex.backend.content.entity.Keyword;
import com.aynikortex.backend.content.enums.ContentType;
import com.aynikortex.backend.content.enums.FileFormat;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ContentMapper {

    public Content toEntity(CreateTextContentRequest request) {
        return Content.builder()
                .title(request.title())
                .contentType(ContentType.TEXT)
                .textContent(request.textContent())
                .build();
    }

    public Content toEntity(
            CreateFileContentRequest request,
            String filePath,
            FileFormat fileFormat
    ) {
        Content content = new Content();

        content.setTitle(request.title());
        content.setContentType(ContentType.FILE);
        content.setFileName(request.file().getOriginalFilename());
        content.setFilePath(filePath);
        content.setFileFormat(fileFormat);

        return content;
    }


    public ContentResponseDTO toResponseDTO(Content content) {
        return new ContentResponseDTO(
                content.getId(),
                content.getTitle(),
                content.getContentType(),
                content.getTextContent(),
                content.getFileName(),
                content.getFileFormat(),
                content.getCategory(),
                content.getSubcategory(),
                content.getConfidence(),
                toKeywordDTO(content.getKeywords()),
                content.getSummary(),
                content.getCreatedAt()
        );
    }


    private List<KeywordDTO> toKeywordDTO(List<Keyword> keywords) {
        if (keywords == null) {
            return List.of();
        }

        return keywords.stream()
                .map(keyword -> new KeywordDTO(
                        keyword.getTerm(),
                        keyword.getScore()
                ))
                .toList();
    }
}