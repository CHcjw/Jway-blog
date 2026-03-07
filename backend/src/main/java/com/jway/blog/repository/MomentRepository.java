package com.jway.blog.repository;

import com.jway.blog.entity.Moment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MomentRepository extends JpaRepository<Moment, Long> {
    List<Moment> findAllByOrderByTimestampDesc();
}
