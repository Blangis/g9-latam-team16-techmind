package com.aynikortex.backend.content.entity;

import com.aynikortex.backend.content.enums.ContentType;
import com.aynikortex.backend.content.enums.FileFormat;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "contents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String title;

    private String summary;

    @Enumerated(EnumType.STRING)
    private ContentType contentType;

    @Column(columnDefinition = "LONGTEXT")
    private String textContent;

    private String fileName;

    private String filePath;

    @Enumerated(EnumType.STRING)
    private FileFormat fileFormat;

    private String category;

    private String subcategory;

    private BigDecimal confidence;

    private String modelVersion;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private List<Keyword> keywords;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}