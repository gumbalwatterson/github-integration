package com.github.api.githubintegration.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Branch {
    @JsonProperty("name")
    public String name;
    @JsonProperty("commit")
    public Commit commit;
}
