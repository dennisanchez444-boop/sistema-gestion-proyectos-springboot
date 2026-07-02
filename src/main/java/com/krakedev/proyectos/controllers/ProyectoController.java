package com.krakedev.proyectos.controllers;

import com.krakedev.proyectos.entidades.Proyecto;
import com.krakedev.proyectos.services.ProyectoService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/proyectos")
// Configuración CORS centralizada requerida por la Fase 1.3 para interactuar con React Vite
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = { "Authorization", "Content-Type" }, methods = {
		RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE })
public class ProyectoController {

	private final ProyectoService service;

	public ProyectoController(ProyectoService service) {
		this.service = service;
	}

	@GetMapping("/publico/resumen") // Endpoint Público Informativo (Fase 1.2) - Debe configurarse en SecurityConfig.java con .permitAll()
	public ResponseEntity<Long> obtenerResumenProyectos() {
		return ResponseEntity.ok(service.contarTotalProyectos());
	}

	@PostMapping
	@PreAuthorize("hasRole('ADMIN')") // Restricción estricta evaluada en el Punto de Control 1 (403 Forbidden para USER)
	public ResponseEntity<Proyecto> guardar(@RequestBody Proyecto proyecto) {
		Proyecto proyectoGuardado = service.insertar(proyecto);
		return ResponseEntity.status(HttpStatus.CREATED).body(proyectoGuardado);
	}

	@GetMapping
	@PreAuthorize("hasAnyRole('ADMIN', 'USER')") // Acceso permitido a ambos roles (Fase 2.2 para pintar la tabla en React)
	public ResponseEntity<List<Proyecto>> listar() {
		return ResponseEntity.ok(service.listarTodos());
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
	public ResponseEntity<Proyecto> buscar(@PathVariable Integer id) {
		return ResponseEntity.ok(service.buscarPorId(id));
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')") 
	public ResponseEntity<Proyecto> actualizar(@PathVariable Integer id, @RequestBody Proyecto proyecto) {
		return ResponseEntity.ok(service.actualizar(id, proyecto));
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Map<String, String>> eliminar(@PathVariable Integer id) {
		service.eliminar(id);
		return ResponseEntity.ok(Map.of("mensaje", "Proyecto eliminado correctamente"));
	}
}