package com.maiphong.springapisecuritystart.services;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.maiphong.springapisecuritystart.dtos.category.CategoryCreateUpdateDTO;
import com.maiphong.springapisecuritystart.dtos.category.CategoryDTO;

public interface CategoryService {
    List<CategoryDTO> findAll();

    CategoryDTO findById(UUID id);

    CategoryDTO create(CategoryCreateUpdateDTO categoryDTO);

    CategoryDTO update(UUID id, CategoryCreateUpdateDTO categoryDTO);

    boolean delete(UUID id);

    // If isSoftDelete is true, perform soft delete => set isDeleted = true,
    // deletedAt = current date time
    // If isSoftDelete is false, perform hard delete => delete from database
    boolean delete(UUID id, boolean isSoftDelete);

    List<CategoryDTO> search(String name);

    Page<CategoryDTO> search(String keyword, Pageable pageable);
}