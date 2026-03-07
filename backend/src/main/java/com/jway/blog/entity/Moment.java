package com.jway.blog.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "moments")
public class Moment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 500)
    private String content;

    @Column(nullable = false)
    private LocalDate timestamp;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String size;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public LocalDate getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDate timestamp) { this.timestamp = timestamp; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }
}
