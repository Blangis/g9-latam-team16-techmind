package com.aynikortex.backend.content.service;

import com.aynikortex.backend.content.dto.request.CreateFileContentRequest;
import com.aynikortex.backend.content.dto.request.CreateTextContentRequest;
import com.aynikortex.backend.content.dto.response.ContentResponseDTO;
import com.aynikortex.backend.content.entity.Content;
import com.aynikortex.backend.content.entity.Keyword;
import com.aynikortex.backend.content.enums.FileFormat;
import com.aynikortex.backend.content.mapper.ContentMapper;
import com.aynikortex.backend.content.repository.ContentRepository;
import com.aynikortex.backend.exception.ContentNotFoundException;
import com.aynikortex.backend.integration.dto.response.ClassificationResponse;
import com.aynikortex.backend.integration.service.DataScienceIntegrationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ContentService {

    private final ContentRepository contentRepository;
    private final ContentMapper contentMapper;
    private final FileStorageService fileStorageService;
    private final DataScienceIntegrationService dataScienceIntegrationService;

    public ContentService(
            ContentRepository contentRepository,
            ContentMapper contentMapper,
            FileStorageService fileStorageService,
            DataScienceIntegrationService dataScienceIntegrationService
    ) {
        this.contentRepository = contentRepository;
        this.contentMapper = contentMapper;
        this.fileStorageService = fileStorageService;
        this.dataScienceIntegrationService = dataScienceIntegrationService;
    }

    public ContentResponseDTO createTextContent(
            CreateTextContentRequest request
    ) {

        // 1. Request → Entity
        Content content = contentMapper.toEntity(request);

        // 2. Primer guardado
        content = contentRepository.save(content);

        // 3. Solicitar clasificación a DS
        ClassificationResponse response =
                dataScienceIntegrationService.classifyText(
                        content.getTitle(),
                        content.getTextContent(),
                        null
                );

        // 4. Aplicar clasificación
        applyClassification(content, response);

        // 5. Segundo guardado
        content = contentRepository.save(content);

        // 6. Entity → Response DTO
        return contentMapper.toResponseDTO(content);
    }

    public ContentResponseDTO createFileContent(
            CreateFileContentRequest request
    ){

        // 1. Determinar el formato del archivo
        FileFormat fileFormat = FileFormat.fromFilename(
                request.file().getOriginalFilename()
        );

        // 2. Guardar físicamente el archivo
        String filePath = fileStorageService.save(request.file());


        // 3. Request → Entity
        Content content = contentMapper.toEntity(
                request,
                filePath,
                fileFormat
        );

        // 4. Primer guardado
        content = contentRepository.save(content);

        // 5. Solicitar clasificación a Data Science
        ClassificationResponse response =
                dataScienceIntegrationService.classifyFile(
                        request.file(),
                        null
                );

        // 6. Aplicar clasificación
        applyClassification(content, response);

        // 7. Segundo guardado
        content = contentRepository.save(content);

        // 8. Entity → Response DTO
        return contentMapper.toResponseDTO(content);
    }

    private void applyClassification(
            Content content,
            ClassificationResponse response
    ) {
        content.setCategory(response.classification().category());
        content.setSubcategory(response.classification().subcategory());
        content.setConfidence(response.classification().confidence());
        content.setSummary(response.classification().summary());
        content.setModelVersion(response.modelVersion());

        content.setKeywords(
                response.classification().keywords()
                        .stream()
                        .map(keyword -> new Keyword(
                                keyword.term(),
                                keyword.score()
                        ))
                        .toList()
        );
    }

    public List<ContentResponseDTO> getAllContents() {

        return contentRepository.findAll()
                .stream()
                .map(contentMapper::toResponseDTO)
                .toList();
    }

    public ContentResponseDTO getContentById(UUID id) {

        Content content = contentRepository.findById(id)
                .orElseThrow(() -> new ContentNotFoundException(id));

        return contentMapper.toResponseDTO(content);
    }

    public List<ContentResponseDTO> searchContentsByTitle(String title){
        return contentRepository
                .findByTitleContainingIgnoreCase(title)
                .stream()
                .map(contentMapper::toResponseDTO)
                .toList();
    }

    public List<ContentResponseDTO> getContentsByCategoryOrSubcategory(String term){
        return contentRepository.findByCategoryIgnoreCaseOrSubcategoryIgnoreCase(term, term)
                .stream()
                .map(contentMapper::toResponseDTO)
                .toList();
    }

    public List<ContentResponseDTO> searchContentsByKeyword(String keyword) {

        return contentRepository.findByKeyword(keyword)
                .stream()
                .map(contentMapper::toResponseDTO)
                .toList();
    }

    public void deleteContent(UUID id) {

        Content content = contentRepository.findById(id)
                .orElseThrow(() -> new ContentNotFoundException(id));

        if (content.getFilePath() != null) {
            fileStorageService.delete(content.getFilePath());
        }

        contentRepository.delete(content);
    }

}