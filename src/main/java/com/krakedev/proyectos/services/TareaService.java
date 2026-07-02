package com.krakedev.proyectos.services;

import com.krakedev.proyectos.entidades.Tarea;
import com.krakedev.proyectos.repositories.TareaRepository; // Asumiendo tu nombre de repositorio
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@Service
public class TareaService {

    private final TareaRepository repository;
    
    private static final Set<String> PRIORIDADES_VALIDAS = Set.of("ALTA", "MEDIA", "BAJA");

    public TareaService(TareaRepository repository) {
        this.repository = repository;
    }

    public Tarea insertar(Tarea tarea) {

        if (tarea.getPrioridad() == null || !PRIORIDADES_VALIDAS.contains(tarea.getPrioridad().toUpperCase())) {
            throw new IllegalArgumentException("Prioridad no válida"); 
        }
        return repository.save(tarea);
    }

    public List<Tarea> listarTodos() {
        return repository.findAll();
    }

    public Tarea buscarPorId(int id) {
        return repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Tarea no encontrada"));
    }

    public Tarea actualizar(int id, Tarea nuevaTarea) {
        if (!repository.existsById(id)) {
            throw new NoSuchElementException("Tarea no encontrada");
        }

        if (nuevaTarea.getPrioridad() == null || !PRIORIDADES_VALIDAS.contains(nuevaTarea.getPrioridad().toUpperCase())) {
            throw new IllegalArgumentException("Prioridad no válida");
        }
        nuevaTarea.setId(id); 
        return repository.save(nuevaTarea);
    }

    public void eliminar(int id) {
        if (!repository.existsById(id)) {
            throw new NoSuchElementException("Tarea no encontrada");
        }
        repository.deleteById(id);
    }
}