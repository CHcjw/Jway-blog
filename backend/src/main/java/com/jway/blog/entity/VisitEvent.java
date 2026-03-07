package com.jway.blog.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "visit_events")
public class VisitEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visitor_id", nullable = false)
    private Visitor visitor;

    @Column(nullable = false)
    private String path;

    @Column(name = "visited_at", nullable = false)
    private LocalDateTime visitedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Visitor getVisitor() { return visitor; }
    public void setVisitor(Visitor visitor) { this.visitor = visitor; }
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
    public LocalDateTime getVisitedAt() { return visitedAt; }
    public void setVisitedAt(LocalDateTime visitedAt) { this.visitedAt = visitedAt; }
}
