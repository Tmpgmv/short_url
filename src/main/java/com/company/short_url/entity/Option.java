package com.company.short_url.entity;

import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.JmixEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.URL;

import java.util.UUID;

@JmixEntity
@Table(name = "SH_OPTION")
@Entity(name = "sh_Option")
public class Option {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @Column(name = "LINK_LIFE_TIME", nullable = false)
    @NotNull
    private Long linkLifeTime;

    @Positive
    @Max(1000)
    @Min(3)
    @Column(name = "URL_PATH_LENGTH", nullable = false)
    @NotNull
    private Integer urlPathLength;

    @Pattern(message = "Url не может оканчиваться на /.", regexp = "^(.*)(?<!/)$")
    @URL
    @Column(name = "SHORTENER_URL", nullable = false)
    @NotNull
    private String shortenerUrl;

    public Long getLinkLifeTime() {
        return linkLifeTime;
    }

    public void setLinkLifeTime(Long linkLifeTime) {
        this.linkLifeTime = linkLifeTime;
    }

    public String getShortenerUrl() {
        return shortenerUrl;
    }

    public void setShortenerUrl(String shortenerUrl) {
        this.shortenerUrl = shortenerUrl;
    }

    public Integer getUrlPathLength() {
        return urlPathLength;
    }

    public void setUrlPathLength(Integer urlPathLength) {
        this.urlPathLength = urlPathLength;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

}