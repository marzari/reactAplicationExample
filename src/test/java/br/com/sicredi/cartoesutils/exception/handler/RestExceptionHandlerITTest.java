package br.com.sicredi.cartoesutils.exception.handler;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.sicredi.cartoesutils.controller.LogRequisicaoApiController;
import br.com.sicredi.cartoesutils.dto.LogRequisicaoApiRequestDTO;
import br.com.sicredi.cartoesutils.exception.LogApiNotFoudException;
import br.com.sicredi.cartoesutils.service.LogRequisicaoApiService;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@WebMvcTest(controllers = LogRequisicaoApiController.class)
public class RestExceptionHandlerITTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private LogRequisicaoApiService service;

	private LogRequisicaoApiRequestDTO dtoRequest;

	@Before
	public void setUp() {
		this.dtoRequest = LogRequisicaoApiRequestDTO.builder()
				.aplicacao("test-api")
				.bodyEnvio("test-body-envio")
				.bodyRetorno("test-body-retorno")
				.codRetorno("200").metodo("GET")
				.url("url:/test/api");

	}

	@Test
	public void whenValidInput_thenReturns200() throws Exception {

		this.mockMvc.perform(post("/log-requisicao-api/save")
				.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.content(this.objectMapper.writeValueAsString(this.dtoRequest)))
		.andExpect(status().isCreated());
	}

	@Test
	public void whenInvalidCodRetorno_thenReturns400() throws Exception {

		this.dtoRequest.codRetorno(null);

		this.mockMvc.perform(post("/log-requisicao-api/save")
				.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.content(this.objectMapper.writeValueAsString(this.dtoRequest)))
		.andExpect(content().string(containsString("Código de retorno não deve estar em branco ou nulo.")))
		.andExpect(status().isBadRequest());
	}

	@Test
	public void whenInvalidAplicacao_thenReturns400() throws Exception {

		this.dtoRequest.aplicacao(null);

		this.mockMvc.perform(post("/log-requisicao-api/save")
				.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.content(this.objectMapper.writeValueAsString(this.dtoRequest)))
		.andExpect(content().string(containsString("Nome da aplicação não deve estar em branco ou nulo.")))
		.andExpect(status().isBadRequest());
	}

	@Test
	public void whenInvalidMetodo_thenReturns400() throws Exception {

		this.dtoRequest.metodo(null);

		this.mockMvc.perform(post("/log-requisicao-api/save")
				.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.content(this.objectMapper.writeValueAsString(this.dtoRequest)))
		.andExpect(content().string(containsString("Método não deve estar em branco ou nulo.")))
		.andExpect(status().isBadRequest());
	}

	@Test
	public void whenInvalidUrl_thenReturns400() throws Exception {

		this.dtoRequest.url(null);

		this.mockMvc.perform(post("/log-requisicao-api/save")
				.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.content(this.objectMapper.writeValueAsString(this.dtoRequest)))
		.andExpect(content().string(containsString("Url não deve estar em branco ou nulo.")))
		.andExpect(status().isBadRequest());
	}

	@Test
	public void whenNoObjects_thenReturns404() throws Exception {

		Mockito.when(this.service.findAll()).thenThrow(new LogApiNotFoudException("Nada encontrado"));
		this.mockMvc.perform(get("/log-requisicao-api/get/all").contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
		.andExpect(status().isNotFound()).andReturn();
	}

	@Test
	public void whenMissingHeaderInDeleteAll_thenReturn401() throws Exception {
		this.mockMvc
		.perform(delete("/log-requisicao-api/delete/all")
				.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
		.andExpect(status().isUnauthorized()).andReturn();
	}

	@Test
	public void whenInvalidId_thenReturn400() throws Exception {
		this.mockMvc.perform(delete("/log-requisicao-api/delete/25498")
				.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.header("token-mandatory-auth", "pass"))
		.andExpect(content().string(containsString("invalid hexadecimal representation of an ObjectId:")))
		.andExpect(status().isBadRequest()).andReturn();
	}

	@Test
	public void whenInvalidTokenAuthorizationInDeleteId_thenReturn401() throws Exception {
		this.mockMvc.perform(delete("/log-requisicao-api/delete/25498")
				.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.header("token-mandatory-auth", "invalid_pass"))
		.andExpect(content().string(containsString("Token de autorização inválido. Acesso negado.")))
		.andExpect(status().isUnauthorized()).andReturn();
	}

	@Test
	public void whenInvalidTokenAuthorizationInDeleteAll_thenReturn401() throws Exception {
		this.mockMvc.perform(delete("/log-requisicao-api/delete/all")
				.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
				.header("token-mandatory-auth", "invalid_pass"))
		.andExpect(content().string(containsString("Token de autorização inválido. Acesso negado.")))
		.andExpect(status().isUnauthorized()).andReturn();
	}

}
