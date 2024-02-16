package com.github.api.githubintegration.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RepositoryDataDto {
    private String owner;
    private String repositoryName;
    private List<BranchDataDto> branches;

}
