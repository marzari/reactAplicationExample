package br.com.sicredi.cartoesutils.dto;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class LogRequisicaoApiRequestDTOTest {

	private static final String MESSAGE = "LogRequisicaoApiRequestDTO(url=url:/test/api, metodo=GET, codRetorno=200, aplicacao=test-api, bodyEnvio=test-body-envio, bodyRetorno=test-body-retorno)";
	private LogRequisicaoApiRequestDTO dtoRequest;

	@Before
	public void setUp() {
		this.dtoRequest = LogRequisicaoApiRequestDTO.builder()
				.aplicacao("test-api")
				.bodyEnvio("test-body-envio")
				.bodyRetorno("test-body-retorno")
				.codRetorno("200")
				.metodo("GET")
				.url("url:/test/api");
	}

	@Test
	public void testToString() {
		assertEquals(MESSAGE, this.dtoRequest.toString());
	}

}
