package com.autoreserve.backend.dto.user;

import java.util.List;

/**
 * DTO para respuestas de listados paginados de usuarios.
 * Encapsula la lista de resultados junto con los metadatos de navegación.
 */
public class PagedUserResponse {

    private List<UserResponse> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;

    /**
     * Constructor para estructurar la respuesta de paginación.
     */
    public PagedUserResponse(
            List<UserResponse> content,
            int page,
            int size,
            long totalElements,
            int totalPages
    ) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }

    /* ================= GETTERS ================= */

    public List<UserResponse> getContent() {
        return content;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }
}