package com.krakedev.proyectos.controllers;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.krakedev.proyectos.JwtUtil;
import com.krakedev.proyectos.entidades.Usuario;
import com.krakedev.proyectos.repositories.UsuarioRepository;
import com.krakedev.proyectos.security.SecurityConfig;
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
	private final UsuarioRepository usuarioRepository;
	private final TokenBlacklistService blackListService;
	private final SecurityConfig securityConfig;

	public AuthController(UsuarioService usuarioService, UsuarioRepository usuarioRepository,
			TokenBlacklistService blackListService, SecurityConfig securityConfig) {
		super();
		this.usuarioService = usuarioService;
		this.usuarioRepository = usuarioRepository;
		this.blackListService = blackListService;
		this.securityConfig = securityConfig;
	}

	@PostMapping("/registrar")
	public ResponseEntity<?> registrar(@RequestBody Usuario usuario) {
		try {
			Usuario usuarioRegistrado = usuarioService.registrar(usuario);
			return ResponseEntity.status(HttpStatus.CREATED).body(usuarioRegistrado);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al registrar el usuario: " + e.getMessage());
		}
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody Usuario usuario) {
		try {
			Usuario usuarioEncontrado = usuarioService.login(usuario.getUsername(), usuario.getPassword());
			if (usuarioEncontrado != null) {
				String token = JwtUtil.generarToken(usuarioEncontrado.getUsername(), usuarioEncontrado.getRol());
				return ResponseEntity.ok("Token: " + token);
			} else {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas");
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al iniciar sesión: " + e.getMessage());
		}
	}

	@GetMapping("/perfil")
	public ResponseEntity<?> perfil(Authentication authentication) {
		try {
			if (authentication == null || !authentication.isAuthenticated()) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");
			}

			String username = authentication.getName();
			String rol = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).findFirst()
					.orElse("");

			if ("ROLE_ADMIN".equalsIgnoreCase(rol)) {
				return ResponseEntity.ok("Bienvenido administrador " + username);
			} else if ("ROLE_USER".equalsIgnoreCase(rol)) {
				return ResponseEntity.ok("Bienvenido usuario " + username);
			} else {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Rol no autorizado: " + rol);
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al procesar el perfil: " + e.getMessage());
		}
	}

	@PostMapping("/logout")
	public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
		try {
			if (authHeader == null || !authHeader.startsWith("Bearer ")) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token no provisto o formato inválido");
			}

			String token = authHeader.substring(7);
			DecodedJWT datosToken = JwtUtil.validarToken(token);

			if (datosToken == null) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido o ya expirado");
			}

			blackListService.invalidarToken(token);
			return ResponseEntity.ok(Map.of("mensaje", "Sesión cerrada exitosamente. Token invalidado."));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error al procesar el cierre de sesión: " + e.getMessage());
		}
	}
}