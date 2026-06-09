package com.krakedev.proyectos.services;

import java.util.Optional;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.krakedev.proyectos.entidades.Usuario;
import com.krakedev.proyectos.repositories.UsuarioRepository;

@Service
public class UsuarioService {

	@Autowired
	private UsuarioRepository repository;

	public Usuario registrar(Usuario usuario) {
		String passwordEncriptada = BCrypt.hashpw(usuario.getPassword(), BCrypt.gensalt());

		usuario.setPassword(passwordEncriptada);
		return repository.save(usuario);
	}

	public Usuario login(String username, String password) {
		Optional<Usuario> usuario = repository.findByUsername(username);

		if (usuario.isPresent()) {
			Usuario u = usuario.get();
			boolean passwordCorrecto = BCrypt.checkpw(password, u.getPassword());
			if (passwordCorrecto) {
				return u;
			}
		}
		return null;
	}

	public boolean autenticar(String username, String password) {

		Optional<Usuario> usuarioEncontrado = repository.findByUsername(username);

		if (usuarioEncontrado.isPresent()) {
			Usuario usuario = usuarioEncontrado.get();

			if (BCrypt.checkpw(password, usuario.getPassword())) {
				return true;
			}
		}
		return false;
	}
}