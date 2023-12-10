package com.gateway.ApiGateway.models;

import lombok.*;

import java.util.Collection;
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String accessToken;
    private String userId;
    private String refreshToken;
    private long expireAt;
    private Collection<String> authorities;
}
