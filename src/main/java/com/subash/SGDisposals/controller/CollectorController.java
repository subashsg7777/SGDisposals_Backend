        package com.subash.SGDisposals.controller;

        import com.subash.SGDisposals.dto.CollectReqDto;
        import com.subash.SGDisposals.dto.CollectedResDto;
        import com.subash.SGDisposals.service.ICollectorService;
        import com.subash.SGDisposals.service.IRequestService;
        import com.subash.SGDisposals.service.IUserService;
        import com.subash.SGDisposals.service.implementation.RequestService;
        import jakarta.validation.Valid;
        import lombok.RequiredArgsConstructor;
        import org.springframework.http.HttpStatus;
        import org.springframework.http.ResponseEntity;
        import org.springframework.web.bind.annotation.*;

        import java.util.Map;

        @RestController
        @RequestMapping("api/v1/collector")
        @RequiredArgsConstructor
        public class CollectorController {

            private final ICollectorService collectorService;
            private final RequestService requestService;

            @PutMapping("collected")
            public ResponseEntity<CollectedResDto> markAsCollected(@RequestParam Long id, @RequestParam Long user_id){

                CollectedResDto map = collectorService.markAsCollected(id, user_id);
                return ResponseEntity.status(HttpStatus.OK).body(map);
            }

            @PutMapping("collect")
            public ResponseEntity<CollectedResDto> collectWaste(@Valid @RequestBody CollectReqDto collectReqDto){
//                Long points = requestService.collectRequest(collectReqDto);
//
//                CollectedResDto map = collectorService.markAsCollected(collectReqDto.getCollection_id(), collectReqDto.getCollector_id());
//                map.setPoints(points);
//                return ResponseEntity.status(HttpStatus.OK).body(map);

                CollectedResDto res = requestService.collectAndMark(collectReqDto);
                return ResponseEntity.status(HttpStatus.OK).body(res);
            }
        }
