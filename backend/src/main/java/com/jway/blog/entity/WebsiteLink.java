package com.jway.blog.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "website_links")
public class WebsiteLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "section_title")
    private String sectionTitle;

    @Column(nullable = false, name = "section_icon")
    private String sectionIcon;

    @Column(nullable = false, name = "section_order")
    private Integer sectionOrder;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(nullable = false)
    private String url;

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(nullable = false, name = "sort_order")
    private Integer sortOrder;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSectionTitle() { return sectionTitle; }
    public void setSectionTitle(String sectionTitle) { this.sectionTitle = sectionTitle; }
    public String getSectionIcon() { return sectionIcon; }
    public void setSectionIcon(String sectionIcon) { this.sectionIcon = sectionIcon; }
    public Integer getSectionOrder() { return sectionOrder; }
    public void setSectionOrder(Integer sectionOrder) { this.sectionOrder = sectionOrder; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
}
