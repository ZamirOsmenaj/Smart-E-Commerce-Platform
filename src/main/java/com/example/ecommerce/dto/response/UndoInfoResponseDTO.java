package com.example.ecommerce.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * Response DTO for undo operation information.
 */
@Data
@Builder
@AllArgsConstructor
public class UndoInfoResponseDTO {
    private int undoableCommandCount;
    private String lastUndoableCommand;
    private boolean hasUndoableCommands;
    private String historySummary;
}