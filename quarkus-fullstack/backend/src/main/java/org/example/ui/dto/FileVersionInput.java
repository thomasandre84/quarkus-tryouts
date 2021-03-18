package org.example.ui.dto;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class FileVersionInput {
    @NotBlank
    private String name;
    @NotBlank
    private String category;
}
