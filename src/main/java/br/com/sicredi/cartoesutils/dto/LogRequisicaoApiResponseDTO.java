package br.com.sicredi.cartoesutils.dto;

import java.time.LocalDateTime;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogRequisicaoApiResponseDTO {

	@ApiModelProperty(required = true, value = "ID de registro individual no BD.")
	private ObjectId  id;
	@ApiModelProperty(required = true, value = "URL completa executada no momento da requisição.")
	private String url;
	@ApiModelProperty(required = true, value = "Metodo de requisição: get, post, delete etc...")
	private String metodo;
	@ApiModelProperty(required = true, value = "Código HTTP status da requisição: 200, 400, 404 etc...")
	private String codRetorno;
	@ApiModelProperty(required = true, value = "Nome da API que esta requisitando o registro do log.")
	private String aplicacao;
	@ApiModelProperty(required = false, value = "Data e hora registradas no momento do registro da entidade no MongoDB.")
	@JsonFormat(pattern ="dd/MM/yyyy HH:mm:ss")
	private LocalDateTime dtAcesso;
	@ApiModelProperty(required = false, value = "Corpo da mensagem enviada.")
	private String bodyEnvio;
	@ApiModelProperty(required = false, value = "Corpo da mensagem recebida.")
	private String bodyRetorno;

	public String getId() {
		return this.id.toHexString();
	}

}
