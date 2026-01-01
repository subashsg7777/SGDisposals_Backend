package com.subash.SGDisposals.service.implementation;

import com.subash.SGDisposals.entity.PointsSystem;
import com.subash.SGDisposals.exception.ResourceNotFoundException;
import com.subash.SGDisposals.repositories.PointsRepo;
import com.subash.SGDisposals.service.IpointsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PointsService implements IpointsService {

    private final PointsRepo pointsRepo;
    @Override
    public List<PointsSystem> getPointsList() {

        List<PointsSystem> pointsLists = pointsRepo.findAll();
        if (pointsLists.isEmpty()) {
            throw new ResourceNotFoundException("No points Lists found");
        }
        return pointsLists;
    }
}
