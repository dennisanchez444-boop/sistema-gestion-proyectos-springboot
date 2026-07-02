package com.krakedev.proyectos.controllers;

import com.krakedev.proyectos.entidades.Empleado;
import com.krakedev.proyectos.services.EmpleadoService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/empleados")
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = { "Authorization", "Content-Type" }, methods = {
		RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE })
public class EmpleadoController {

	private final EmpleadoService service;

	public EmpleadoController(EmpleadoService service) {
		this.service = service;
	}

	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<?> guardar(@RequestBody Empleado empleado) {
		try {
			Empleado empleadoGuardado = service.insertar(empleado);
			return ResponseEntity.status(HttpStatus.CREATED).body(empleadoGuardado);
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@GetMapping
	@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
	public ResponseEntity<?> listar() {
		try {
			List<Empleado> empleados = service.listarTodos();
			return ResponseEntity.ok(empleados);
		} catch (RuntimeException e) {
			return ResponseEntity.internalServerError().body("Error al listar empleados");
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> buscar(@PathVariable int id) {
		try {
			Optional<Empleado> empleado = service.buscarPorId(id);
			if (empleado == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Empleado no encontrado");
			}
			return ResponseEntity.ok(empleado);
		} catch (RuntimeException e) {
			return ResponseEntity.internalServerError().body("Error al buscar el empleado");
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> actualizar(@PathVariable int id, @RequestBody Empleado empleado) {
		try {
			Empleado empleadoActualizado = service.actualizar(id, empleado);
			return ResponseEntity.ok(empleadoActualizado);
		} catch (RuntimeException e) {
			return ResponseEntity.internalServerError().body("Error al actualizar empleado");
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> eliminar(@PathVariable int id) {
		try {
			boolean eliminado = service.eliminar(id);
			if (!eliminado) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Empleado no encontrado");
			}
			return ResponseEntity.ok("Empleado eliminado correctamente");
		} catch (RuntimeException e) {
			return ResponseEntity.internalServerError().body("Error al eliminar Empleado");
		}
	}
}