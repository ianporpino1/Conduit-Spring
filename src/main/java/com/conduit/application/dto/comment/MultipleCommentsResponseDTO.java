package com.conduit.application.dto.comment;

import java.util.List;

public record MultipleCommentsResponseDTO(List<CommentDTO> comments) {
}
