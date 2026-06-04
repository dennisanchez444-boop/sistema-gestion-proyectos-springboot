package com.krakedev.proyectos.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.krakedev.proyectos.entidades.Tarea;
import com.krakedev.proyectos.repositories.TareaRepository;

@Service
public class TareaService {
	private TareaRepository repositorio;

	public TareaService(TareaRepository repositorio) {
		this.repositorio = repositorio;
	}

	public List<Tarea> listarTodos() {
		return repositorio.findAll();
	}

	public Tarea insertar(Tarea tarea) {
		return repositorio.save(tarea);
	}

	public Optional<Tarea> buscarPorId(int id) {
		return repositorio.findById(id);

	}

	public Tarea actualizar(int id, Tarea tarea) {
		Optional<Tarea> existeT = buscarPorId(id);
		if (existeT != null) {
			return repositorio.save(tarea);
		} else {
			return null;
		}
	}

	public boolean eliminar(int id) {
		if (repositorio.existsById(id)) {
			repositorio.deleteById(id);
			return true;
		}
		return false;
	}
}
