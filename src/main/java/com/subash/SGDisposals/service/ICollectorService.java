package com.subash.SGDisposals.service;

import com.subash.SGDisposals.dto.CollectedResDto;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

public interface ICollectorService {

    CollectedResDto markAsCollected(Long id, Long user_id);
}
