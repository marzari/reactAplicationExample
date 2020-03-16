package br.com.sicredi.cartoesutils.model;

import java.time.LocalDateTime;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.Setter;

@Document(collection = "logrequisicaoapi")
public class LogRequisicaoApi {

	@Id
	@Getter @Setter private ObjectId  id;
	@Getter @Setter private String url;
	@Getter @Setter private String metodo;
	@Getter @Setter private String codRetorno;
	@Getter @Setter private String aplicacao;
	@Getter @Setter private String bodyEnvio;
	@Getter @Setter private String bodyRetorno;
	@Getter private LocalDateTime dtAcesso;

	public LogRequisicaoApi() {
		this.dtAcesso = LocalDateTime.now();
	}


}
