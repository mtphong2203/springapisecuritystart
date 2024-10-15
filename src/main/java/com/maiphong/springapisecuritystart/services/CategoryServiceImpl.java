package com.maiphong.springapisecuritystart.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.maiphong.springapisecuritystart.dtos.category.CategoryCreateUpdateDTO;
import com.maiphong.springapisecuritystart.dtos.category.CategoryDTO;
import com.maiphong.springapisecuritystart.entities.Category;
import com.maiphong.springapisecuritystart.repositories.CategoryRepository;

import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository _categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this._categoryRepository = categoryRepository;
    }

    @Override
    public List<CategoryDTO> findAll() {
        var categories = _categoryRepository.findAll();

        var categoriesDTOs = categories.stream().map(c -> {
            var categoryDTO = new CategoryDTO();
            categoryDTO.setId(c.getId());
            categoryDTO.setName(c.getName());
            categoryDTO.setDescription(c.getDescription());
            categoryDTO.setActive(c.isActive());

            return categoryDTO;
        }).toList();

        return categoriesDTOs;
    }

    @Override
    public CategoryDTO findById(UUID id) {
        var category = _categoryRepository.findById(id).orElse(null);

        if (category == null) {
            return null;
        }

        var categoryDTO = new CategoryDTO();
        categoryDTO.setId(category.getId());
        categoryDTO.setName(category.getName());
        categoryDTO.setDescription(category.getDescription());
        categoryDTO.setActive(category.isActive());
        return categoryDTO;
    }

    @Override
    public CategoryDTO create(CategoryCreateUpdateDTO categoryCreateDTO) {
        if (categoryCreateDTO == null) {
            throw new IllegalArgumentException("CategoryDTO is required");
        }

        var exist = _categoryRepository.findByName(categoryCreateDTO.getName());
        if (exist != null) {
            throw new IllegalArgumentException("CategoryDTO is exist!");
        }

        var category = new Category();
        category.setName(categoryCreateDTO.getName());
        category.setDescription(categoryCreateDTO.getDescription());
        category.setActive(categoryCreateDTO.isActive());
        category.setCreateAt(LocalDateTime.now());
        category.setDeleted(false);

        _categoryRepository.save(category);

        var updateCategoryDTO = new CategoryDTO();
        updateCategoryDTO.setId(category.getId());
        updateCategoryDTO.setName(category.getName());
        updateCategoryDTO.setDescription(category.getDescription());
        updateCategoryDTO.setActive(category.isActive());
        return updateCategoryDTO;
    }

    @Override
    public CategoryDTO update(UUID id, CategoryCreateUpdateDTO categoryDTO) {
        if (categoryDTO == null) {
            throw new IllegalArgumentException("CategoryDTO is required");
        }

        // Checl if category name is existed
        var existedCategory = _categoryRepository.findByName(categoryDTO.getName());
        if (existedCategory != null && !existedCategory.getId().equals(id)) {
            throw new IllegalArgumentException("Category name is existed");
        }

        // Find category by id - Managed
        var category = _categoryRepository.findById(id).orElse(null);

        if (category == null) {
            throw new IllegalArgumentException("Category not found");
        }

        // Update category
        category.setName(categoryDTO.getName());
        category.setDescription(categoryDTO.getDescription());
        category.setUpdateAt(LocalDateTime.now());
        category.setActive(categoryDTO.isActive());

        // Save category => update
        category = _categoryRepository.save(category);

        // Convert Category to CategoryDTO
        var updatedCategoryDTO = new CategoryDTO();
        updatedCategoryDTO.setId(category.getId());
        updatedCategoryDTO.setName(category.getName());
        updatedCategoryDTO.setDescription(category.getDescription());
        updatedCategoryDTO.setActive(category.isActive());

        return updatedCategoryDTO;
    }

    @Override
    public boolean delete(UUID id) {
        var category = _categoryRepository.findById(id).orElse(null);

        if (category == null) {
            throw new IllegalArgumentException("Category not found");
        }
        _categoryRepository.delete(category);

        // Check if category entity is deleted
        var isDeleted = _categoryRepository.findById(id).isEmpty();

        return isDeleted;
    }

    @Override
    public boolean delete(UUID id, boolean isSoftDelete) {
        var existingCategory = _categoryRepository.findById(id).orElse(null);

        if (existingCategory == null) {
            throw new IllegalArgumentException("Category not found");
        }

        if (isSoftDelete) {
            existingCategory.setDeleted(true);
            existingCategory.setDeleteAt(LocalDateTime.now());
            _categoryRepository.save(existingCategory);

            // Check if category entity is soft deleted
            var deletedCategory = _categoryRepository.findById(id).orElse(null);
            if (deletedCategory != null && deletedCategory.isDeleted()) {
                return true;
            } else {
                return false;
            }
        } else {

            _categoryRepository.delete(existingCategory);
            // Check if category entity is deleted
            var isDeleted = _categoryRepository.findById(id).isEmpty();

            return isDeleted;
        }
    }

    @Override
    public List<CategoryDTO> search(String keyword) {
        // Find category by keyword
        Specification<Category> specification = (root, query, criteriaBuilder) -> {
            // Neu keyword null thi tra ve null
            if (keyword == null) {
                return null;
            }

            // Neu keyword khong null
            // WHERE LOWER(name) LIKE %keyword%
            Predicate namePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("name")),
                    "%" + keyword.toLowerCase() + "%");

            // WHERE LOWER(description) LIKE %keyword%
            Predicate desPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("description")),
                    "%" + keyword.toLowerCase() + "%");

            // WHERE LOWER(name) LIKE %keyword% OR LOWER(description) LIKE %keyword%
            return criteriaBuilder.or(namePredicate, desPredicate);
        };

        var categories = _categoryRepository.findAll(specification);

        // Covert List<Category> to List<CategoryDTO>
        var categoryDTOs = categories.stream().map(category -> {
            var categoryDTO = new CategoryDTO();
            categoryDTO.setId(category.getId());
            categoryDTO.setName(category.getName());
            categoryDTO.setDescription(category.getDescription());
            return categoryDTO;
        }).toList();

        return categoryDTOs;

    }

    @Override
    public Page<CategoryDTO> search(String keyword, Pageable pageable) {
        Specification<Category> specification = (root, query, criteriaBuilder) -> {
            if (keyword == null) {
                return null;
            }

            // WHERE name LIKE %keyword% OR description LIKE %keyword%
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + keyword.toLowerCase() + "%"),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("description")),
                            "%" + keyword.toLowerCase() + "%"));
        };

        Page<Category> categories = _categoryRepository.findAll(specification, pageable);

        Page<CategoryDTO> categoryDTOs = categories.map(category -> {
            var categoryDTO = new CategoryDTO();
            categoryDTO.setId(category.getId());
            categoryDTO.setName(category.getName());
            categoryDTO.setDescription(category.getDescription());
            return categoryDTO;
        });

        return categoryDTOs;
    }

}
