package com.github.api.githubintegration.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BranchDataDto {
    private String branchName;
    private String sha;
}
