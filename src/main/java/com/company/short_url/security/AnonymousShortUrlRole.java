package com.company.short_url.security;

import com.company.short_url.entity.ShortUrl;
import io.jmix.security.model.EntityAttributePolicyAction;
import io.jmix.security.role.annotation.EntityAttributePolicy;
import io.jmix.security.role.annotation.EntityPolicy;
import io.jmix.security.role.annotation.ResourceRole;
import io.jmix.security.model.EntityPolicyAction;


@ResourceRole(name = "Anonymous ShortUrl Role", code = AnonymousShortUrlRole.CODE, scope = "API")
public interface AnonymousShortUrlRole {
    String CODE = "anonymous-short-url-role";

    @EntityAttributePolicy(entityClass = ShortUrl.class, attributes = "*", action = EntityAttributePolicyAction.VIEW)
    @EntityPolicy(entityClass = ShortUrl.class, actions = {
            EntityPolicyAction.READ,
            EntityPolicyAction.UPDATE
            })
    void shortUrl();
}