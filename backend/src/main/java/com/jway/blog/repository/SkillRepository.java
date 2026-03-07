package com.jway.blog.repository;

import com.jway.blog.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SkillRepository extends JpaRepository<Skill, Long> {
    List<Skill> findAllByTypeOrderByNameAsc(int type);
}
