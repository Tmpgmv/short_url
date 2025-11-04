package com.company.short_url.security;

import com.company.short_url.entity.ShortUrl;
import io.jmix.security.role.annotation.JpqlRowLevelPolicy;
import io.jmix.security.role.annotation.RowLevelRole;

@RowLevelRole(
        name = "UserCanManageOnlyTheirShortLinks",
        code = UserCanManageOnlyTheirShortLinksRole.CODE)
public interface UserCanManageOnlyTheirShortLinksRole {
    String CODE = "user-can-manage-only-their-short-links";

    @JpqlRowLevelPolicy(entityClass = ShortUrl.class, where = "{E}.createdBy.id = :current_user_id")
    void shortUrl();
}
