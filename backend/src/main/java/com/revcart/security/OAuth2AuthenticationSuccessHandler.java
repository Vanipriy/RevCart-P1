package com.revcart.security;

import com.revcart.entity.User;
import com.revcart.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Optional;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserRepository userRepository;
    
    @org.springframework.beans.factory.annotation.Value("${frontend.url}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        try {
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            
            String email = oAuth2User.getAttribute("email");
            String name = oAuth2User.getAttribute("name");
            
            System.out.println("OAuth2 login successful for: " + email);
            
            // Find or create user
            Optional<User> userOptional = userRepository.findByEmail(email);
            User user;
            
            if (userOptional.isPresent()) {
                user = userOptional.get();
                System.out.println("Existing user found: " + user.getId());
            } else {
                // Create new user
                user = new User();
                user.setEmail(email);
                user.setName(name);
                user.setRole(User.Role.CUSTOMER);
                user.setEmailVerified(true);
                user.setPassword("OAUTH_USER");
                user = userRepository.save(user);
                System.out.println("New OAuth user created: " + user.getId());
            }
            
            // Generate JWT token
            String token = jwtUtils.generateTokenFromEmail(email);
            
            // Redirect to frontend with token and user data
            String targetUrl = UriComponentsBuilder.fromUriString(frontendUrl + "/oauth2/redirect")
                    .queryParam("token", token)
                    .queryParam("id", user.getId())
                    .queryParam("name", user.getName())
                    .queryParam("email", user.getEmail())
                    .queryParam("role", user.getRole().toString())
                    .build().toUriString();
            
            System.out.println("Redirecting to: " + targetUrl);
            getRedirectStrategy().sendRedirect(request, response, targetUrl);
        } catch (Exception e) {
            System.err.println("OAuth2 authentication error: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect(frontendUrl + "/login?error=oauth_failed");
        }
    }
}