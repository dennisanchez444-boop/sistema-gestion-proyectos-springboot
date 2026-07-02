package com.krakedev.proyectos.controllers;

import com.krakedev.proyectos.entidades.Proyecto;
import com.krakedev.proyectos.services.ProyectoService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/proyectos")

@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = { "Authorization", "Content-Type" }, methods = {
		RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE })
public class ProyectoController {

	private final ProyectoService service;

	public ProyectoController(ProyectoService service) {
		this.service = service;
	}

	@GetMapping("/publico/resumen")
	public ResponseEntity<Long> obtenerResumenProyectos() {
		try {
			Long totalProyectos = service.contarTotalProyectos();
			return ResponseEntity.ok(totalProyectos);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(0L);
		}
	}

	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> guardar(@RequestBody Proyecto proyecto) {
		try {
			Proyecto proyectoGuardado = service.insertar(proyecto);
			return ResponseEntity.status(HttpStatus.CREATED).body(proyectoGuardado);
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@GetMapping
	@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
	public ResponseEntity<?> listar() {
		try {
			List<Proyecto> proyectos = service.listarTodos();
			return ResponseEntity.ok(proyectos);
		} catch (RuntimeException e) {
			return ResponseEntity.internalServerError().body("Error al listar proyectos");
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> buscar(@PathVariable int id) {
		try {
			Optional<Proyecto> proyecto = service.buscarPorId(id);
			if (proyecto.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Proyecto no encontrado");
			}
			return ResponseEntity.ok(proyecto.get());
		} catch (RuntimeException e) {
			return ResponseEntity.internalServerError().body("Error al buscar el proyecto");
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> actualizar(@PathVariable int id, @RequestBody Proyecto proyecto) {
		try {
			Proyecto proyectoActualizado = service.actualizar(id, proyecto);
			if (proyectoActualizado == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Proyecto no encontrado");
			}
			return ResponseEntity.ok(proyectoActualizado);
		} catch (RuntimeException e) {
			return ResponseEntity.internalServerError().body("Error al actualizar proyecto");
		}
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> eliminar(@PathVariable int id) {
		try {
			boolean eliminado = service.eliminar(id);
			if (!eliminado) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Proyecto no encontrado");
			}
			return ResponseEntity.ok("Proyecto eliminado correctamente");
		} catch (RuntimeException e) {
			return ResponseEntity.internalServerError().body("Error al eliminar proyecto");
		}
	}
}