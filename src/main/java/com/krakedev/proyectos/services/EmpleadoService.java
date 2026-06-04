package com.krakedev.proyectos.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.krakedev.proyectos.entidades.Empleado;
import com.krakedev.proyectos.repositories.EmpleadoRepository;

@Service
public class EmpleadoService {
	private EmpleadoRepository repositorio;

	public EmpleadoService(EmpleadoRepository repositorio) {
		this.repositorio = repositorio;
	}

	public List<Empleado> listarTodos() {
		return repositorio.findAll();
	}

	public Empleado insertar(Empleado empleado) {
		return repositorio.save(empleado);
	}

	public Optional<Empleado> buscarPorId(int id) {
		return repositorio.findById(id);

	}

	public Empleado actualizar(int id, Empleado empleado) {
		Optional<Empleado> existeE = buscarPorId(id);
		if (existeE != null) {
			return repositorio.save(empleado);
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
