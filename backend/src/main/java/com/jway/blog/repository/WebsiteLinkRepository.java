package com.jway.blog.repository;

import com.jway.blog.entity.WebsiteLink;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WebsiteLinkRepository extends JpaRepository<WebsiteLink, Long> {
    List<WebsiteLink> findAllByOrderBySectionOrderAscSortOrderAscIdAsc();
}
