package com.subash.SGDisposals.controller;

import com.subash.SGDisposals.dto.AllUserRequestDto;
import com.subash.SGDisposals.entity.CollectionRequest;
import com.subash.SGDisposals.entity.PointsSystem;
import com.subash.SGDisposals.service.IRequestService;
import com.subash.SGDisposals.service.IpointsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1/collections")
@RequiredArgsConstructor
public class CollectionController {

    private final IRequestService requestService;
    private final IpointsService pointsService;

    @GetMapping("requests")
    public ResponseEntity<?> getAllRequestOfaCollector(){

        List<AllUserRequestDto> response = requestService.getAllRequestsForCollector();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("waste-lists")
    public ResponseEntity<?> getWasteListOfaCollector(){
        List<PointsSystem> pointsSystemList = pointsService.getPointsList();
        return ResponseEntity.status(HttpStatus.OK).body(pointsSystemList);
    }
}
