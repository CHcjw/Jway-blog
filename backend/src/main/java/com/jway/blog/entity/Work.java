package com.jway.blog.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "works")
public class Work {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, name = "image_url")
    private String imageUrl;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(nullable = false)
    private String period;

    @Column(nullable = false, name = "team_size")
    private String teamSize;

    @Lob
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String techs;

    @Lob
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String points;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }
    public String getTeamSize() { return teamSize; }
    public void setTeamSize(String teamSize) { this.teamSize = teamSize; }
    public String getTechs() { return techs; }
    public void setTechs(String techs) { this.techs = techs; }
    public String getPoints() { return points; }
    public void setPoints(String points) { this.points = points; }
}
