package authservice.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record CreateUserResponseDto (
    String username,
    UUID idUser
){}
