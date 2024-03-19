package com.generation.blogpessoal.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.generation.blogpessoal.model.UsuarioLogin;
import com.generation.blogpessoal.model.UsuarioModel;
import com.generation.blogpessoal.repository.UsuarioRepository;
import com.generation.blogpessoal.security.JwtService;

@Service
public class UsuarioService {

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private AuthenticationManager authenticationManager;

	public Optional<UsuarioModel> cadastrarUsuario(UsuarioModel usuarioModel) {

		if (usuarioRepository.findByUsuario(usuarioModel.getUsuario()).isPresent())
			return Optional.empty();

		usuarioModel.setSenha(criptografarSenha(usuarioModel.getSenha()));

		return Optional.of(usuarioRepository.save(usuarioModel));

	}

	public Optional<UsuarioModel> atualizarUsuario(UsuarioModel usuarioModel) {

		if (usuarioRepository.findById(usuarioModel.getId()).isPresent()) {

			Optional<UsuarioModel> buscaUsuario = usuarioRepository.findByUsuario(usuarioModel.getUsuario());

			if ((buscaUsuario.isPresent()) && (buscaUsuario.get().getId() != usuarioModel.getId()))
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuário já existe!", null);

			usuarioModel.setSenha(criptografarSenha(usuarioModel.getSenha()));

			return Optional.ofNullable(usuarioRepository.save(usuarioModel));

		}

		return Optional.empty();

	}

	public Optional<UsuarioLogin> autenticarUsuario(Optional<UsuarioLogin> usuarioLogin) {

		// Gera o Objeto de autenticação
		var credenciais = new UsernamePasswordAuthenticationToken(usuarioLogin.get().getUsuario(),
				usuarioLogin.get().getSenha());

		// Autentica o Usuario
		Authentication authentication = authenticationManager.authenticate(credenciais);

		// Se a autenticação foi efetuada com sucesso
		if (authentication.isAuthenticated()) {

			// Busca os dados do usuário
			Optional<UsuarioModel> usuarioModel = usuarioRepository.findByUsuario(usuarioLogin.get().getUsuario());

			// Se o usuário foi encontrado
			if (usuarioModel.isPresent()) {

				// Preenche o Objeto usuarioLogin com os dados encontrados
				usuarioLogin.get().setId(usuarioModel.get().getId());
				usuarioLogin.get().setNome(usuarioModel.get().getNome());
				usuarioLogin.get().setFoto(usuarioModel.get().getFoto());
				usuarioLogin.get().setToken(gerarToken(usuarioLogin.get().getUsuario()));
				usuarioLogin.get().setSenha("");

				// Retorna o Objeto preenchido
				return usuarioLogin;

			}

		}

		return Optional.empty();

	}

	private String criptografarSenha(String senha) {

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

		return encoder.encode(senha);

	}

	private String gerarToken(String usuario) {
		return "Bearer " + jwtService.generateToken(usuario);
	}

}
