package br.com.sicredi.cartoesutils.dto;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class LogRequisicaoApiRequestDTO {

	@NotBlank(message = "{url.not.blank}")
	@ApiModelProperty(required = true, value = "URL completa executada no momento da requisição.")
	private String url;
	@NotBlank(message = "{metodo.not.blank}")
	@ApiModelProperty(required = true, value = "Metodo de requisição: get, post, delete etc...")
	private String metodo;
	@NotBlank(message = "{codigo.not.blank}")
	@ApiModelProperty(required = true, value = "Código HTTP status da requisição: 200, 400, 404 etc...")
	private String codRetorno;
	@NotBlank(message = "{aplicacao.not.blank}")
	@ApiModelProperty(required = true, value = "Nome da API que esta requisitando o registro do log.")
	private String aplicacao;
	@ApiModelProperty(required = false, value = "Corpo da mensagem enviada.")
	private String bodyEnvio;
	@ApiModelProperty(required = false, value = "Corpo da mensagem recebida.")
	private String bodyRetorno;

	public static LogRequisicaoApiRequestDTO builder() {
		return new LogRequisicaoApiRequestDTO();
	}

	public LogRequisicaoApiRequestDTO url(String url) {
		this.url = url;
		return this;
	}

	public LogRequisicaoApiRequestDTO metodo(String metodo) {
		this.metodo = metodo;
		return this;
	}

	public LogRequisicaoApiRequestDTO codRetorno(String codRetorno) {
		this.codRetorno = codRetorno;
		return this;
	}

	public LogRequisicaoApiRequestDTO aplicacao(String aplicacao) {
		this.aplicacao = aplicacao;
		return this;
	}

	public LogRequisicaoApiRequestDTO bodyEnvio(String bodyEnvio) {
		this.bodyEnvio = bodyEnvio;
		return this;
	}

	public LogRequisicaoApiRequestDTO bodyRetorno(String bodyRetorno) {
		this.bodyRetorno = bodyRetorno;
		return this;
	}

}
