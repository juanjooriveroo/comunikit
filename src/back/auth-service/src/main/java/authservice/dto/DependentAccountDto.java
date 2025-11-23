package authservice.dto;

import authservice.entity.Language;
import lombok.Builder;

import java.util.UUID;

@Builder
public record DependentAccountDto (
        UUID id,
        String name,
        String username,
        Language language,
        Float storage_used
){}