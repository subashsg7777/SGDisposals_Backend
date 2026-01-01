package com.subash.SGDisposals.service;

import com.subash.SGDisposals.dto.AllUserRequestDto;
import com.subash.SGDisposals.dto.CollectReqDto;
import com.subash.SGDisposals.dto.CollectedResDto;
import com.subash.SGDisposals.entity.CollectionRequest;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IRequestService {

    List<AllUserRequestDto> getAllRequestsForUser(@RequestParam Long id);
    List<AllUserRequestDto> getAllRequestsForCollector();
    Long collectRequest(CollectReqDto collectReqDto);
}
