package com.subash.SGDisposals.repositories;

import com.subash.SGDisposals.entity.PointsSystem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointsRepo extends JpaRepository<PointsSystem,Long> {
}
