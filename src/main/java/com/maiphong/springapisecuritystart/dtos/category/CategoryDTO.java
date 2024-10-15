package com.maiphong.springapisecuritystart.dtos.category;

import java.util.UUID;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {
    private UUID id;

    @NotNull(message = "Name is required")
    @NotBlank(message = "Name is not empty")
    @Length(min = 5, max = 500, message = "3->500 characters")
    private String name;

    @Length(max = 500, message = "maximum 500 characters")
    private String description;

    @NotNull(message = "Active is required")
    private boolean isActive;

}