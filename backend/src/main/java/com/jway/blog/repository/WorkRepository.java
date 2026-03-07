package com.jway.blog.repository;

import com.jway.blog.entity.Work;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkRepository extends JpaRepository<Work, Long> {
}
