package com.subash.SGDisposals.controller;

import com.subash.SGDisposals.dto.AllUserRequestDto;
import com.subash.SGDisposals.entity.CollectionRequest;
import com.subash.SGDisposals.entity.PointsSystem;
import com.subash.SGDisposals.service.IRequestService;
import com.subash.SGDisposals.service.IpointsService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v2/collections")
@RequiredArgsConstructor
public class CollectionController {

    private static final Logger log = LoggerFactory.getLogger(CollectionController.class);

    private final IRequestService requestService;
    private final IpointsService pointsService;

    @GetMapping("requests")
    public ResponseEntity<?> getAllRequestOfaCollector(){
        log.info("CollectionController.requests - entered. Authentication: {}", SecurityContextHolder.getContext().getAuthentication());

        List<AllUserRequestDto> response = requestService.getAllRequestsForCollector();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("waste-lists")
    public ResponseEntity<?> getWasteListOfaCollector(){
        List<PointsSystem> pointsSystemList = pointsService.getPointsList();
        return ResponseEntity.status(HttpStatus.OK).body(pointsSystemList);
    }
}
