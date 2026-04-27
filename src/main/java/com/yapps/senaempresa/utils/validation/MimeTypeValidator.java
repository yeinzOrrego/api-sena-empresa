package com.yapps.senaempresa.utils.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.tika.Tika;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.List;

public class MimeTypeValidator implements ConstraintValidator<AllowedMimeTypes, MultipartFile> {

    private List<String> allowedTypes;
    private final Tika tika = new Tika();

    @Override
    public void initialize(AllowedMimeTypes constraintAnnotation) {
        this.allowedTypes = List.of(constraintAnnotation.types());
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) {
            return true;
        }

        try (BufferedInputStream bis = new BufferedInputStream(file.getInputStream())) {

            String realMimeType = tika.detect(bis);
            return allowedTypes.contains(realMimeType);
        } catch (IOException e) {
            return false;
        }
    }
}
