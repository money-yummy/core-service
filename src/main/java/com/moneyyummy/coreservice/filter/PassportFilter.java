package com.moneyyummy.coreservice.filter;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.moneyyummy.coreservice.config.PassportFilterCondition;
import com.moneyyummy.coreservice.model.UserModel;
import com.moneyyummy.coreservice.util.JwtTokenUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Conditional(PassportFilterCondition.class)
public class PassportFilter extends OncePerRequestFilter {

    private final ObjectMapper mapper;
    private final JwtTokenUtils jwtTokenUtils;
    private final AntPathMatcher antPathMatcher;

    @Value("${moneyyummy.url.skip}")
    private List<String> skipUrl;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String passport = request.getHeader("passport");
        Claims claims = jwtTokenUtils.validateAndExtractTokenClaims(passport);
        String userInfoDto = claims.get("userInfo", String.class);
        UserModel userModel = mapper.readValue(userInfoDto, UserModel.class);
        request.setAttribute("userModel", userModel);
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return skipUrl.stream()
                .anyMatch(pattern -> antPathMatcher.match(pattern, request.getRequestURI()));
    }
}
