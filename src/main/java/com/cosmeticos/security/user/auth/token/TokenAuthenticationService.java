package com.cosmeticos.security.user.auth.token;

import com.cosmeticos.model.User;
import com.cosmeticos.repository.UserRepository;
import com.cosmeticos.security.tokenapi.TokenService;
import com.cosmeticos.security.user.auth.api.UserAuthenticationService;
import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PRIVATE;

@Service
@AllArgsConstructor(access = PACKAGE)
@FieldDefaults(level = PRIVATE, makeFinal = true)
public final class TokenAuthenticationService implements UserAuthenticationService {
    @NonNull
    TokenService tokens;

    @NonNull
    UserRepository users;

    @Override
    public Optional<String> login(final String username, final String password) {
        return users
                .findByEmail(username)
                .filter(user -> Objects.equals(password, user.getPassword()))
                .map(user -> tokens.expiring(ImmutableMap.of("username", username)));
    }

    @Override
    public Optional<User> findByToken(final String token) {
        return Optional
                .of(tokens.verify(token))
                .map(map -> map.get("username"))
                .flatMap(users::findByEmail);
    }

    @Override
    public void logout(final User user) {
        // Nothing to doy
    }
}