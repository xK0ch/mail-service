package de.fynnkoch.mailservice.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${cors.allowed-origins:http://localhost:4200}")
    private List<String> allowedOrigins;

    @Value("${mail.api-key:changeme}")
    private String apiKey;

    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(allowedOrigins.toArray(String[]::new))
                .allowedMethods("GET", "POST", "OPTIONS")
                .allowedHeaders("*");
    }

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        // The transactional send endpoint is internal, server-to-server only. It is guarded by
        // a shared API key so it can never be misused as an open relay, even if exposed.
        registry.addInterceptor(new ApiKeyInterceptor(apiKey)).addPathPatterns("/api/send");
    }

    private static final class ApiKeyInterceptor implements HandlerInterceptor {

        private final byte[] expectedKey;

        private ApiKeyInterceptor(String apiKey) {
            this.expectedKey = apiKey.getBytes(StandardCharsets.UTF_8);
        }

        @Override
        public boolean preHandle(@NonNull HttpServletRequest request,
                                 @NonNull HttpServletResponse response,
                                 @NonNull Object handler) {
            String provided = request.getHeader("X-Api-Key");
            if (provided != null
                    && MessageDigest.isEqual(provided.getBytes(StandardCharsets.UTF_8), expectedKey)) {
                return true;
            }
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
    }
}
