package com.subash.SGDisposals.dto;

import com.subash.SGDisposals.StatusEnum;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
public class AllUserRequestDto {

    private String name;
    private Long user_id;
    private Long id;
    private String address;
    private StatusEnum status;
    private Instant created_at;
}
