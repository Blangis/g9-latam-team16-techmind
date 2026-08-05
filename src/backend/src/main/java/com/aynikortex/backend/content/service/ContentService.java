package com.aynikortex.backend.content.service;

import com.aynikortex.backend.content.dto.ContentResponseDTO;
import com.aynikortex.backend.content.mapper.ContentMapper;
import com.aynikortex.backend.content.repository.ContentRepository;
import com.aynikortex.backend.content.entity.Content;
import com.aynikortex.backend.content.enums.ContentType;
import com.aynikortex.backend.integration.dto.response.ClassificationResponse;
import com.aynikortex.backend.integration.service.DataScienceIntegrationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ContentService {

    private final ContentRepository contentRepository;
    private final ContentMapper contentMapper;
    private final DataScienceIntegrationService dataScienceService;

    public ContentService(ContentRepository contentRepository,
                          ContentMapper contentMapper,
                          DataScienceIntegrationService dataScienceService) {
        this.contentRepository = contentRepository;
        this.contentMapper = contentMapper;
        this.dataScienceService = dataScienceService;
    }

    @Transactional
    public ContentResponseDTO createContent(ContentRequestDTO requestDTO) {
        if (requestDTO.contentType() == ContentType.TEXT){
            if (requestDTO.textContent() == null || requestDTO.textContent().trim().isEmpty()){
                throw new IllegalArgumentException("Text content is required for TEXT type");
            }
        } else if (requestDTO.contentType() == ContentType.FILE) {
            if (requestDTO.file() == null && (requestDTO.fileName() == null || requestDTO.filePath() == null)){
                throw new IllegalArgumentException("File content is required for FILE type");
            }
        } else {
            throw new IllegalArgumentException("Invalid content type");
        }

        Content content = contentMapper.toEntity(requestDTO);
        content.setCreatedAt(LocalDateTime.now());
        Content savedContent = contentRepository.save(content);

        try {
            ClassificationResponse dsResponse;

            if (requestDTO.contentType() == ContentType.TEXT) {
                Map<String, Object> textData = Map.of("text", requestDTO.textContent());

                dsResponse = dataScienceService.classifyText(
                        savedContent.getId().toString(),
                        requestDTO.title(),
                        textData
                );
            } else {
                Map<String, Object> fileMetadata = Map.of(
                        "id", savedContent.getId().toString(),
                        "title", requestDTO.title() != null ? requestDTO.title() : "Sin título"
                );

                dsResponse = dataScienceService.classifyFile(
                        requestDTO.file(),
                        fileMetadata
                );
            }

            if (dsResponse != null && "SUCCESS".equalsIgnoreCase(dsResponse.status())) {
                var classification = dsResponse.classification();

                if (classification != null) {
                    savedContent.setCategory(classification.category());
                    savedContent.setSubCategory(classification.subcategory());

                    if (classification.confidence() != null) {
                        savedContent.setConfidence(classification.confidence().doubleValue());
                    }
                }

                savedContent.setModelVersion(dsResponse.modelVersion());
                savedContent.setUpdatedAt(LocalDateTime.now());

                savedContent = contentRepository.save(savedContent);
            }

        } catch (Exception e) {
            throw new RuntimeException("Error al comunicarse con el servicio de Ciencia de Datos: " + e.getMessage(), e);
        }

        return contentMapper.toResponseDTO(savedContent);
    }

    @Transactional(readOnly = true)
    public List<ContentResponseDTO> getAllContents() {
        return contentRepository.findAll().stream()
                .map(contentMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ContentResponseDTO getContentById(UUID id) {
        Content content = contentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contenido no encontrado con ID: " + id));
        return contentMapper.toResponseDTO(content);
    }

    @Transactional
    public void deleteContent(UUID id) {
        if (!contentRepository.existsById(id)) {
            throw new RuntimeException("No se puede eliminar, contenido no encontrado con ID: " + id);
        }
        contentRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<ContentResponseDTO> searchContentsByTitle(String title) {
        return contentRepository.findByTitleContainingIgnoreCase(title).stream()
                .map(contentMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ContentResponseDTO> getContentsByCategoryOrSubcategory(String term) {
        return contentRepository.findByCategoryContainingIgnoreCaseOrSubcategoryContainingIgnoreCase(term, term).stream()
                .map(contentMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ContentResponseDTO> searchContentsByKeyword(String keyword) {
        String queryKeyword = keyword.toLowerCase();
        return contentRepository.findAll().stream()
                .filter(content -> content.getKeywords() != null &&
                        content.getKeywords().stream()
                                .anyMatch(k -> k.getWord() != null && k.getWord().toLowerCase().contains(queryKeyword)))
                .map(contentMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}