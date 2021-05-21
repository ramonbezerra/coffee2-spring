package br.edu.uepb.coffee.settings;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import io.jsonwebtoken.Jwts;

public class AuthorizationFilter extends BasicAuthenticationFilter {
    
    public AuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    protected void doFilterInternal(HttpServletRequest request, 
            HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        
        String header = request.getHeader("Authorization");
        
        if (header == null || !header.startsWith("Bearer")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        UsernamePasswordAuthenticationToken authenticationToken = getAuthentication(request);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        
        if (token != null) {
            String user = Jwts.parser().setSigningKey("UEPBProgWeb20202MySecretKeyToGenJWTsToken".getBytes())
                    .parseClaimsJws(token.replace("Bearer ",""))
                    .getBody()
                    .getSubject();
            if (user != null) {
                return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
            }
            return null;
        }

        return null;
    }
}
