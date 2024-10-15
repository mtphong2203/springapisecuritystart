package com.maiphong.springapisecuritystart.dtos.category;

import org.hibernate.validator.constraints.Length;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryCreateUpdateDTO {
    @NotNull(message = "Name is required")
    @NotBlank(message = "Name is not empty")
    @Length(min = 5, max = 500, message = "3->500 characters")
    private String name;

    @Length(max = 500, message = "maximum 500 characters")
    private String description;

    @NotNull(message = "Active is required")
    private boolean isActive;
}