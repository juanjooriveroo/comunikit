package authservice.dto;

import java.util.List;

public record GetAllDependentsAccountsResponseDto (
        List<DependentAccountDto> accounts
){}