package com.substring.irctc.config.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.SignatureException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private JwtHelper jwtHelper;
    private UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtHelper jwtHelper, UserDetailsService userDetailsService) {
        this.jwtHelper = jwtHelper;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        //her ek request se pahle

        //Bearer 213534cdslkhfafjwfghjsfgopjfpk[f
        String authorizationHeader = request.getHeader("Authorization");

        //if we use cookie then here token get karenge cookie se

        logger.trace("Request to the JWT filter: {}", authorizationHeader);

        String username = null;
        String token = null;

        if(authorizationHeader != null  && authorizationHeader.startsWith("Bearer ")){

            // Extracting the token from the header

            try {
                token = authorizationHeader.substring(7); // Remove "Bearer " prefix
                username = jwtHelper.getUsernameFromToken(token);

                logger.trace("user name : {}",username);

                if(username!=null && SecurityContextHolder.getContext().getAuthentication() == null) {


                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    if(jwtHelper.isTokenValid(token,userDetails)){
                        //SeucrityContext:

                        UsernamePasswordAuthenticationToken authentication=
                                new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());

                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        SecurityContextHolder.getContext().setAuthentication(authentication);

                        logger.trace("Authentication set in the Security Context");
                    }
                }
            }

            catch (IllegalArgumentException ex){
                logger.error("Unable to get JWT Token: {}",ex);
                ex.printStackTrace();
            }catch (ExpiredJwtException ex){
                logger.error("JWT Token has expired: {}",ex);
                ex.printStackTrace();
            }catch (MalformedJwtException ex){
                logger.error("Invalid JWT Token: {}",ex);
                ex.printStackTrace();
            }catch (Exception e) {
                logger.error("Invalid Token: {}",e);
                e.printStackTrace();
            }


            // Check if the token is valid


        }

        else {
            System.out.println("Invalid Authorization Header");
        }

        //ye request ko age bhej dega
        filterChain.doFilter(request, response);


    }
}
