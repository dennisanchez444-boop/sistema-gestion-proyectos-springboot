package com.krakedev.proyectos.controllers;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.krakedev.proyectos.JwtUtil;
import com.krakedev.proyectos.entidades.Usuario;
import com.krakedev.proyectos.services.TokenBlacklistService;
import com.krakedev.proyectos.services.UsuarioService;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = { "Authorization", "Content-Type" }, methods = {
		RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE })
public class AuthController {

	private final UsuarioService usuarioService;
	private final TokenBlacklistService blackListService;

	// Inyección limpia por constructor (removimos dependencias no utilizadas en esta clase)
	public AuthController(UsuarioService usuarioService, TokenBlacklistService blackListService) {
		this.usuarioService = usuarioService;
		this.blackListService = blackListService;
	}

	@PostMapping("/registrar")
	public ResponseEntity<Usuario> registrar(@RequestBody Usuario usuario) {
		Usuario usuarioRegistrado = usuarioService.registrar(usuario);
		return ResponseEntity.status(HttpStatus.CREATED).body(usuarioRegistrado);
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody Usuario usuario) {
		Usuario usuarioEncontrado = usuarioService.login(usuario.getUsername(), usuario.getPassword());
		
		if (usuarioEncontrado != null) {
			String token = JwtUtil.generarToken(usuarioEncontrado.getUsername(), usuarioEncontrado.getRol());
			
			// SOLUCIÓN AL CRASH: Retornamos un objeto clave-valor para que React pueda interpretarlo como JSON
			return ResponseEntity.ok(Map.of(
				"token", token,
				"role", usuarioEncontrado.getRol() // Devuelve 'ADMIN' o 'USER' para alimentar el Navbar de inmediato
			));
		}
		
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Credenciales incorrectas"));
	}

	@GetMapping("/perfil")
	public ResponseEntity<Map<String, String>> perfil(Authentication authentication) {
		if (authentication == null || !authentication.isAuthenticated()) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Usuario no autenticado"));
		}

		String username = authentication.getName();
		String rol = authentication.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.findFirst()
				.orElse("");

		if ("ROLE_ADMIN".equalsIgnoreCase(rol) || "ADMIN".equalsIgnoreCase(rol)) {
			return ResponseEntity.ok(Map.of("mensaje", "Bienvenido administrador " + username));
		} else if ("ROLE_USER".equalsIgnoreCase(rol) || "USER".equalsIgnoreCase(rol)) {
			return ResponseEntity.ok(Map.of("mensaje", "Bienvenido usuario " + username));
		}
		
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Rol no autorizado: " + rol));
	}

	@PostMapping("/logout")
	public ResponseEntity<Map<String, String>> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Token no provisto o formato inválido"));
		}

		String token = authHeader.substring(7);
		DecodedJWT datosToken = JwtUtil.validarToken(token);

		if (datosToken == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Token inválido o ya expirado"));
		}

		blackListService.invalidarToken(token);
		return ResponseEntity.ok(Map.of("mensaje", "Sesión cerrada exitosamente. Token invalidado."));
	}
}