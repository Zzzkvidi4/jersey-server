package com.zzzkvidi4.server;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public final class NamedRequest {
    @Nullable
    @JsonProperty(required = true)
    private String name;
}
