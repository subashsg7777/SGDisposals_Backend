package com.subash.SGDisposals.dto;


import lombok.Data;

@Data
public class CollectedResDto {

    private String message;
    private Long request_id;
    private String collector;
    private Long points;
}
