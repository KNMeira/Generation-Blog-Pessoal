package com.generation.blogpessoal.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.generation.blogpessoal.model.UsuarioModel;
import com.generation.blogpessoal.repository.UsuarioRepository;
import com.generation.blogpessoal.service.UsuarioService;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UsuarioControllerTest {
	
	@Autowired
	private TestRestTemplate testRestTemplate;
	
	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@BeforeAll
	void start(){
		
		usuarioRepository.deleteAll();
		
		usuarioService.cadastrarUsuario(new UsuarioModel(0L,
				"Root", "root@root.com", "rootroot", ""));
		
	}
	
	@Test
	@DisplayName("Cadastrar um Usuário")
	public void deveCriarUmUsuario() {
		
		HttpEntity<UsuarioModel> corpoRequisicao = new HttpEntity<UsuarioModel>(new UsuarioModel(0L,
				"Valentina", "valentina@email.com.br", "valentina123", "-"));
		
		ResponseEntity<UsuarioModel> corpoResposta = testRestTemplate
				.exchange("/usuarios/cadastrar", HttpMethod.POST, corpoRequisicao, UsuarioModel.class);
		
		assertEquals(HttpStatus.CREATED, corpoResposta.getStatusCode());
	}
	
	@Test
	@DisplayName("Não deve permitir duplicação do Usuário")
	public void naoDeveDuplicarUsuario() {
		
		usuarioService.cadastrarUsuario(new UsuarioModel(0L,
				"Ada Lovelace", "ada_lovelace@email.com.br", "Ada123456", "-"));
		
		HttpEntity<UsuarioModel> corpoRequisicao = new HttpEntity<UsuarioModel>(new UsuarioModel(0L,
				"Ada Lovelace", "ada_lovelace@email.com.br", "Ada123456", "-"));
		
		ResponseEntity<UsuarioModel> corpoResposta = testRestTemplate
				.exchange("/usuarios/cadastrar", HttpMethod.POST, corpoRequisicao, UsuarioModel.class);
		
		assertEquals(HttpStatus.BAD_REQUEST, corpoResposta.getStatusCode());	
	}
	
	@Test
	@DisplayName("Atualizar um Usuário")
	public void deveAtualizarUmUsuario() {
		
		Optional<UsuarioModel> usuarioCadastrado = usuarioService.cadastrarUsuario(new UsuarioModel(0L,
				"Karl Marx", "karl_marx@email.com.br", "karl12345", "-"));
		
		UsuarioModel usuarioUpdate = new UsuarioModel(usuarioCadastrado.get().getId(),
				"Karl Marx", "karl_marx@email.com.br", "karl12345", "-");
		
		HttpEntity<UsuarioModel> corpoRequisicao = new HttpEntity<UsuarioModel>(usuarioUpdate);
		
		ResponseEntity<UsuarioModel> corpoResposta = testRestTemplate
				.withBasicAuth("root@root.com", "rootroot")
				.exchange("/usuarios/atualizar", HttpMethod.PUT, corpoRequisicao, UsuarioModel.class);
		
		assertEquals(HttpStatus.OK, corpoResposta.getStatusCode());
					
	}
	
	@Test
	@DisplayName("Listar todos os Usuários")
	public void deveMostrarTodosUsuarios() {
		
		usuarioService.cadastrarUsuario(new UsuarioModel(0L,
				"Graham Bell", "graham_bell@email.com", "bell123456", "-"));
		
		usuarioService.cadastrarUsuario(new UsuarioModel(0L,
				"Michel Foucault", "michel_focault@email.com", "focault123", "-"));
		
		ResponseEntity<String> resposta = testRestTemplate
				.withBasicAuth("root@root.com", "rootroot")
				.exchange("/usuarios/all", HttpMethod.GET, null, String.class);
		
		assertEquals(HttpStatus.OK, resposta.getStatusCode());
		
	}

}
