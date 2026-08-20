package com.aynikortex.backend.content.controller;

import com.aynikortex.backend.content.dto.request.CreateFileContentRequest;
import com.aynikortex.backend.content.dto.request.CreateTextContentRequest;
import com.aynikortex.backend.content.dto.response.ContentResponseDTO;
import com.aynikortex.backend.content.service.ContentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/contents")
public class ContentController {

    private final ContentService contentService;

    public ContentController(ContentService contentService) {
        this.contentService = contentService;
    }

    @PostMapping("/text")
    public ResponseEntity<ContentResponseDTO> createTextContent(
            @Valid @RequestBody CreateTextContentRequest request
    ) {
        ContentResponseDTO response =
                contentService.createTextContent(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping(value = "/file", consumes = "multipart/form-data")
    public ResponseEntity<ContentResponseDTO> createFileContent(
            @Valid @ModelAttribute CreateFileContentRequest request
    ) {
        ContentResponseDTO response =
                contentService.createFileContent(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<ContentResponseDTO>> getAllContents() {
        return ResponseEntity.ok(
                contentService.getAllContents()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContentResponseDTO> getContentById(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(
                contentService.getContentById(id)
        );
    }

    @GetMapping("/search/title")
    public ResponseEntity<List<ContentResponseDTO>> searchByTitle(
            @RequestParam String title
    ) {
        return ResponseEntity.ok(
                contentService.searchContentsByTitle(title)
        );
    }

    @GetMapping("/search/category")
    public ResponseEntity<List<ContentResponseDTO>> getByCategory(
            @RequestParam String term
    ) {
        return ResponseEntity.ok(
                contentService.getContentsByCategoryOrSubcategory(term)
        );
    }

    @GetMapping("/search/keyword")
    public ResponseEntity<List<ContentResponseDTO>> searchByKeyword(
            @RequestParam String q
    ) {
        return ResponseEntity.ok(
                contentService.searchContentsByKeyword(q)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContent(
            @PathVariable UUID id
    ) {
        contentService.deleteContent(id);
        return ResponseEntity.noContent().build();
    }
}