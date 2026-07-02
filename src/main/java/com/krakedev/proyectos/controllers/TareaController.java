package com.krakedev.proyectos.controllers;

import com.krakedev.proyectos.entidades.Tarea;
import com.krakedev.proyectos.services.TareaService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = { "Authorization", "Content-Type" }, methods = {
		RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE })
@RequestMapping("/api/tareas")
public class TareaController {

	private final TareaService service;

	public TareaController(TareaService service) {
		this.service = service;
	}

	@PostMapping
	@PreAuthorize("hasRole('ADMIN')") 
	public ResponseEntity<Tarea> guardar(@RequestBody Tarea tarea) {
		Tarea tareaGuardada = service.insertar(tarea);
		return ResponseEntity.status(HttpStatus.CREATED).body(tareaGuardada);
	}

	@GetMapping
	@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
	public ResponseEntity<List<Tarea>> listar() {
		return ResponseEntity.ok(service.listarTodos());
	}

	@GetMapping("/{id}")
	public ResponseEntity<Tarea> buscar(@PathVariable int id) {
		return ResponseEntity.ok(service.buscarPorId(id));
	}

	@PutMapping("/{id}")
	public ResponseEntity<Tarea> actualizar(@PathVariable int id, @RequestBody Tarea tarea) {
		return ResponseEntity.ok(service.actualizar(id, tarea));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Map<String, String>> eliminar(@PathVariable int id) {
		service.eliminar(id);
		return ResponseEntity.ok(Map.of("mensaje", "Tarea eliminada correctamente"));
	}
}