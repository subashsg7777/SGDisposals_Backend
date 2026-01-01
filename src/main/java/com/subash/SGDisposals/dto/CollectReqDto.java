package com.subash.SGDisposals.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class CollectReqDto {

    @NotNull
    private Long user_id;

    @NotNull
    private Long collector_id;

    @NotNull
    private Long collection_id;

    @NotNull
    private Map<String,Long> weights;
}
