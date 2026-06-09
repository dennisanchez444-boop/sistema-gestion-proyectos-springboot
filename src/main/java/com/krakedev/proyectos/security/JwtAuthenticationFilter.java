package com.krakedev.proyectos.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.krakedev.proyectos.JwtUtil;
import com.krakedev.proyectos.services.TokenBlacklistService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final TokenBlacklistService blackListService;

	public JwtAuthenticationFilter(TokenBlacklistService blackListService) {
		this.blackListService = blackListService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String authHeader = request.getHeader("Authorization");

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}

		String token = authHeader.substring(7);

		if (blackListService.estaInvalidado(token)) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write("Acceso denegado: Sesión cerrada");
			return;
		}

		DecodedJWT datosToken = JwtUtil.validarToken(token);
		if (datosToken == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write("Acceso denegado: Token inválido o expirado");
			return;
		}

		String username = datosToken.getSubject();
		String rolOriginal = datosToken.getClaim("rol").asString();

		String rolSpring = "ROLE_" + rolOriginal;
		SimpleGrantedAuthority authority = new SimpleGrantedAuthority(rolSpring);

		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, null,
				Collections.singleton(authority));

		SecurityContextHolder.getContext().setAuthentication(authentication);

		filterChain.doFilter(request, response);
	}
}