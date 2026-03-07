package com.jway.blog.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "visitors")
public class Visitor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "visitor_key", nullable = false, unique = true)
    private String visitorKey;

    @Column(name = "first_visit_at", nullable = false)
    private LocalDateTime firstVisitAt;

    @Column(name = "last_visit_at", nullable = false)
    private LocalDateTime lastVisitAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getVisitorKey() { return visitorKey; }
    public void setVisitorKey(String visitorKey) { this.visitorKey = visitorKey; }
    public LocalDateTime getFirstVisitAt() { return firstVisitAt; }
    public void setFirstVisitAt(LocalDateTime firstVisitAt) { this.firstVisitAt = firstVisitAt; }
    public LocalDateTime getLastVisitAt() { return lastVisitAt; }
    public void setLastVisitAt(LocalDateTime lastVisitAt) { this.lastVisitAt = lastVisitAt; }
}
