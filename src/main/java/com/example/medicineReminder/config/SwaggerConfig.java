package com.example.medicineReminder.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig
{
    // application.yml/properties 파일에서 OAuth2 공급자의 정보를 가져옵니다.
    @Value("${spring.security.oauth2.client.provider.kakao.authorization-uri}")
    private String authorizationUri;

    @Value("${spring.security.oauth2.client.provider.kakao.token-uri}")
    private String tokenUri;

    // 예시: provider-name은 google, kakao 등 application.yml에 설정한 값으로 변경해야 합니다.

    @Bean
    public OpenAPI openAPI()
    {
        Info info = new Info()
                .title("내 애플리케이션 API")
                .version("v1.0.0")
                .description("API에 대한 설명입니다.");

        String securitySchemeName = "OAuth 2.0";

        SecurityRequirement securityRequirement = new SecurityRequirement().addList(securitySchemeName);

        Components components = new Components()
                .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                        .name(securitySchemeName)
                        .type(SecurityScheme.Type.OAUTH2) // 타입을 OAUTH2로 변경
                        .flows(new OAuthFlows()
                                .authorizationCode(new OAuthFlow() // Authorization Code Grant Flow 사용
                                                .authorizationUrl(authorizationUri) // 인증 URL
                                                .tokenUrl(tokenUri) // 토큰 요청 URL
                                        // 필요한 스코프를 여기에 추가
                                        // .scopes(new Scopes().addString("read", "읽기 권한").addString("write", "쓰기 권한"))
                                )
                        )
                );

        return new OpenAPI()
                .info(new Info().title("내 애플리케이션 API").version("v1.0.0"))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                        )
                );

    }
}
