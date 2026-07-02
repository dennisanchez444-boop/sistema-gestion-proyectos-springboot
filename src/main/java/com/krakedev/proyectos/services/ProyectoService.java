package com.krakedev.proyectos.services;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import com.krakedev.proyectos.entidades.Proyecto;
import com.krakedev.proyectos.repositories.ProyectoRepository;

@Service
public class ProyectoService {

	private final ProyectoRepository repositorio;

	// Inyección por constructor (Buenas prácticas: inmutable y fácil de testear)
	public ProyectoService(ProyectoRepository repositorio) {
		this.repositorio = repositorio;
	}

	public List<Proyecto> listarTodos() {
		return repositorio.findAll();
	}

	public Proyecto insertar(Proyecto proyecto) {
		// Aseguramos que la fecha se asigne al momento de la persistencia si viene nula
		if (proyecto.getFechaInicio() == null) {
			proyecto.setFechaInicio(LocalDate.now());
		}
		return repositorio.save(proyecto);
	}

	public Proyecto buscarPorId(int id) {
		// Clean Code: Si existe lo retorna, si no, lanza la excepción que capturará el GlobalExceptionHandler
		return repositorio.findById(id)
				.orElseThrow(() -> new NoSuchElementException("Proyecto con ID " + id + " no encontrado"));
	}

	public Long contarTotalProyectos() {
		return repositorio.count(); // Resuelve eficientemente el requerimiento de métricas de la Fase 1.2
	}

	public Proyecto actualizar(int id, Proyecto proyecto) {
		// Reutilizamos existsById para una verificación rápida antes de actualizar
		if (!repositorio.existsById(id)) {
			throw new NoSuchElementException("No se puede actualizar: Proyecto no encontrado");
		}
		
		proyecto.setId(id); 
		if (proyecto.getFechaInicio() == null) {
			proyecto.setFechaInicio(LocalDate.now());
		}
		return repositorio.save(proyecto);
	}
	
	public void eliminar(int id) {
		if (!repositorio.existsById(id)) {
			throw new NoSuchElementException("No se puede eliminar: Proyecto no encontrado");
		}
		repositorio.deleteById(id);
	}
}