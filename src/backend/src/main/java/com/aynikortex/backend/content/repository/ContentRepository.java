package com.aynikortex.backend.content.repository;

import com.aynikortex.backend.content.entity.Content;
import com.aynikortex.backend.content.enums.ContentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ContentRepository extends JpaRepository<Content, UUID> {
    List<Content> findByTitleContainingIgnoreCase(String title);
    List<Content> findByCategoryIgnoreCaseOrSubcategoryIgnoreCase(String category, String subcategory);

    @Query(value = """
        SELECT *
        FROM contents
        WHERE JSON_SEARCH(
            keywords,
            'one',
            :keyword,
            NULL,
            '$[*].term'
        ) IS NOT NULL
        """, nativeQuery = true)
    List<Content> findByKeyword(@Param("keyword") String keyword);
}