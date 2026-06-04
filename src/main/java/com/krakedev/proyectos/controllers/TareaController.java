package com.krakedev.proyectos.controllers;

import com.krakedev.proyectos.entidades.Tarea;
import com.krakedev.proyectos.services.TareaService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tareas")
public class TareaController {

	private final TareaService service;

	public TareaController(TareaService service) {
		this.service = service;
	}

	@PostMapping
	public ResponseEntity<?> guardar(@RequestBody Tarea tarea) {
		try {
			Tarea tareaGuardada = service.insertar(tarea);
			return ResponseEntity.status(HttpStatus.CREATED).body(tareaGuardada);
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@GetMapping
	public ResponseEntity<?> listar() {
		try {
			List<Tarea> tareas = service.listarTodos();
			return ResponseEntity.ok(tareas);
		} catch (RuntimeException e) {
			return ResponseEntity.internalServerError().body("Error al listar tareas");
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> buscar(@PathVariable int id) {
		try {
			Optional<Tarea> tarea = service.buscarPorId(id);

			if (tarea.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tarea no encontrada");
			}

			return ResponseEntity.ok(tarea.get());

		} catch (RuntimeException e) {
			return ResponseEntity.internalServerError().body("Error al buscar la tarea");
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> actualizar(@PathVariable int id, @RequestBody Tarea tarea) {
		try {
			Tarea tareaActualizada = service.actualizar(id, tarea);

			if (tareaActualizada == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tarea no encontrada");
			}

			return ResponseEntity.ok(tareaActualizada);

		} catch (RuntimeException e) {
			return ResponseEntity.internalServerError().body("Error al actualizar la tarea");
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> eliminar(@PathVariable int id) {
		try {
			boolean eliminado = service.eliminar(id);

			if (!eliminado) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tarea no encontrada");
			}

			return ResponseEntity.ok("Tarea eliminada correctamente");

		} catch (RuntimeException e) {
			return ResponseEntity.internalServerError().body("Error al eliminar la tarea");
		}
	}
}