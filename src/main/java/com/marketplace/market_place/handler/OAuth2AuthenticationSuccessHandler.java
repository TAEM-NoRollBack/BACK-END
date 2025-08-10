package com.marketplace.market_place.handler;

import com.marketplace.market_place.domain.Role;
import com.marketplace.market_place.domain.User;
import com.marketplace.market_place.dto.OAuthAttributes;
import com.marketplace.market_place.dto.SessionUser;
import com.marketplace.market_place.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        String registrationId = token.getAuthorizedClientRegistrationId();

        // Use a dummy userNameAttributeName as it's not critical for our logic here
        OAuthAttributes attributes = OAuthAttributes.of(registrationId, "id", oAuth2User.getAttributes());

        User user = userRepository.findByProviderAndProviderId(attributes.getProvider(), attributes.getProviderId())
                .orElseThrow(() -> new IllegalArgumentException("A user with the given provider details was not found."));

        HttpSession session = request.getSession();
        session.setAttribute("user", new SessionUser(user));

        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority(Role.GUEST.getKey()))) {
            response.sendRedirect("/signup");
        } else {
            response.sendRedirect("/");
        }
    }
}
