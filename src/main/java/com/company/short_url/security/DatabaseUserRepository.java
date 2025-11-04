package com.company.short_url.security;

import com.company.short_url.entity.User;
import io.jmix.securitydata.user.AbstractDatabaseUserRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Primary
@Component("sh_UserRepository")
public class DatabaseUserRepository extends AbstractDatabaseUserRepository<User> {

    @Override
    protected Class<User> getUserClass() {
        return User.class;
    }

    @Override
    protected void initSystemUser(final User systemUser) {
        final Collection<GrantedAuthority> authorities = getGrantedAuthoritiesBuilder()
                .addResourceRole(FullAccessRole.CODE)
                .build();
        systemUser.setAuthorities(authorities);
    }

//    @Override
//    protected void initAnonymousUser(final User anonymousUser) {
//    }

    @Override
    protected void initAnonymousUser(User anonymousUser) {
        super.initAnonymousUser(anonymousUser);

        Collection<GrantedAuthority> authorities = getGrantedAuthoritiesBuilder()
                .addResourceRole("anonymous-short-url-role")
                .build();

        anonymousUser.setAuthorities(authorities);
    }
}