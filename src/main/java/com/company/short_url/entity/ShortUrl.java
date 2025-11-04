package com.company.short_url.entity;

import com.company.short_url.app.OptionCache;
import io.jmix.core.DeletePolicy;
import io.jmix.core.annotation.DeletedBy;
import io.jmix.core.annotation.DeletedDate;
import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.entity.annotation.OnDelete;
import io.jmix.core.metamodel.annotation.JmixEntity;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.checkerframework.common.aliasing.qual.Unique;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@JmixEntity
@Table(name = "SH_SHORT_URL", indexes = {
        @Index(name = "IDX_SH_SHORT_URL_CREATED_BY", columnList = "CREATED_BY_ID")
})
@Entity(name = "sh_ShortUrl")
public class ShortUrl {


    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @CreatedBy
    @JoinColumn(name = "CREATED_BY_ID", nullable = false)
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User createdBy;

    @DeletedBy
    @Column(name = "DELETED_BY")
    private String deletedBy;

    @DeletedDate
    @Temporal(TemporalType.DATE)
    @Column(name = "DELETED_DATE")
    private Date deletedDate;

    @Column(name = "DEADLINE", nullable = false)
    @NotNull
    private Date deadline;

    @PostConstruct
    public void initDeadline(OptionCache optionCache) {
        if (deadline == null && optionCache != null && optionCache.getOption() != null) {
            this.deadline = new Date(System.currentTimeMillis() +
                    optionCache.getOption().getLinkLifeTime() * 1000 * 60);
        }
    }

    @PositiveOrZero
    @Column(name = "REDIRECT_LIMIT")
    private Integer redirectLimit;

    @Length(min = 3, max = 1024)
    @Column(name = "SHORT_URL", nullable = false)
    @Unique
    private String shortUrl;

    @CreatedDate
    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;


    @PositiveOrZero
    @Column(name = "ACCESS_COUNT", nullable = false)
    private Integer accessCount = 0;

    @Column(name = "COMMENT_")
    @Lob
    private String comment;

    @URL(message = "Неверный URL")
    @NotBlank
    @NotEmpty
    @Length(min = 3, max = 1024)
    @Column(name = "ORIGINAL_URL", nullable = false)
    @NotNull
    private String originalUrl;

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }


    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public Integer getRedirectLimit() {
        return redirectLimit;
    }

    public void setRedirectLimit(Integer redirectLimit) {
        this.redirectLimit = redirectLimit;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getAccessCount() {
        return accessCount;
    }

    public void setAccessCount(Integer accessCount) {
        this.accessCount = accessCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public String getDeletedBy() {
        return deletedBy;
    }

    public void setDeletedBy(String deletedBy) {
        this.deletedBy = deletedBy;
    }

    public Date getDeletedDate() {
        return deletedDate;
    }

    public void setDeletedDate(Date deletedDate) {
        this.deletedDate = deletedDate;
    }
}