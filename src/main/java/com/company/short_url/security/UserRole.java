package com.company.short_url.security;

import com.company.short_url.entity.ShortUrl;
import io.jmix.security.model.EntityAttributePolicyAction;
import io.jmix.security.model.EntityPolicyAction;
import io.jmix.security.role.annotation.EntityAttributePolicy;
import io.jmix.security.role.annotation.EntityPolicy;
import io.jmix.security.role.annotation.ResourceRole;
import io.jmix.securityflowui.role.UiFilterRole;
import io.jmix.securityflowui.role.annotation.MenuPolicy;
import io.jmix.securityflowui.role.annotation.ViewPolicy;

@ResourceRole(name = "User", code = UserRole.CODE)
public interface UserRole extends UiFilterRole {
    String CODE = "user";

    @EntityAttributePolicy(entityClass = ShortUrl.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = ShortUrl.class, actions = EntityPolicyAction.ALL)
    void shortUrl();

    @MenuPolicy(menuIds = "sh_ShortUrl.list")
    @ViewPolicy(viewIds = {"sh_ShortUrl.list", "sh_ShortUrl.detail"})
    void screens();
}