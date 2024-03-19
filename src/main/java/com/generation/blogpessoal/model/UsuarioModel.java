package com.generation.blogpessoal.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "tb_usuarios")
public class UsuarioModel {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotNull(message = "O atributo nome é obrigatório")
	private String nome;
	
	@NotNull(message = "O atributo usuário é obrigatório")
	@Email(message = "O atributo usuario deve ser um e-mail válido")
	private String usuario;
	
	@NotBlank(message = "O atributo senha é obrigatório")
	@Size(min = 8, message = "O atributo senha deve ter no minimo 8 caracteres")
	private String senha;
	
	@Size(max = 5000, message = "O atributo foto não pode ser maior que 5000 caracteres")
	private String foto;
	
	@OneToMany(mappedBy = "usuarioModel", cascade = CascadeType.REMOVE)
	@JsonIgnoreProperties("usuarioModel")
	private List<PostagemModel> postagemModel;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public String getFoto() {
		return foto;
	}

	public void setFoto(String foto) {
		this.foto = foto;
	}

	public List<PostagemModel> getPostagemModel() {
		return postagemModel;
	}

	public void setPostagemModel(List<PostagemModel> postagemModel) {
		this.postagemModel = postagemModel;
	}
	
	
	
}
