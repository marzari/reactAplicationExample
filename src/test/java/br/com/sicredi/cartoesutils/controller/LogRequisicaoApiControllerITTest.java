package br.com.sicredi.cartoesutils.controller;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.sicredi.cartoesutils.dto.LogRequisicaoApiRequestDTO;
import br.com.sicredi.cartoesutils.dto.LogRequisicaoApiResponseDTO;
import br.com.sicredi.cartoesutils.mapper.MapperUtil;
import br.com.sicredi.cartoesutils.model.LogRequisicaoApiResponseTestModel;
import br.com.sicredi.cartoesutils.repository.LogRequisicaoApiRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LogRequisicaoApiControllerITTest {

	private static final String URL_TEST_API = "url:/test/api";

	private static final String GET = "GET";

	private static final String _200 = "200";

	private static final String BODY_RETORNO = "test-body-retorno";

	private static final String BODY_ENVIO = "test-body-envio";

	private static final String APLICACAO = "test-api";

	@Autowired
	private WebTestClient webTestClient;

	@Autowired
	private LogRequisicaoApiRepository repository;

	@Autowired
	private MapperUtil map;

	@Rule
	public ExpectedException expected = ExpectedException.none();

	@Test
	public void testDeleteById() throws Exception {
		// Limpa a base para o teste
		this.repository.deleteAll().block();

		// Cria o objeto e insere na base, verificando no retorno se o objeto criado é o mesmo enviado.
		LogRequisicaoApiRequestDTO dtoRequest = this.createRequestDTO();
		byte[] responseBody = this.webTestClient.post().uri("/cartoesutils-log-requisicao-api/log-requisicao-api/save")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.body(Mono.just(dtoRequest), LogRequisicaoApiRequestDTO.class)
				.exchange()
				.expectStatus()
				.isCreated()
				.expectBody()
				.jsonPath("$.url").isEqualTo(URL_TEST_API)
				.jsonPath("$.metodo").isEqualTo(GET)
				.jsonPath("$.codRetorno").isEqualTo(_200).returnResult().getResponseBody();
		LogRequisicaoApiResponseTestModel dtoResponse = jsonAsDto(responseBody);

		//Recupera o objeto da base pelo id, verificando assim que o mesmo esta persistido
		this.webTestClient.get().uri("/cartoesutils-log-requisicao-api/log-requisicao-api/get/" + dtoResponse.getId())
		.accept(MediaType.APPLICATION_JSON)
		.exchange()
		.expectStatus()
		.isOk()
		.expectBody()
		.jsonPath("$.id").isEqualTo(dtoResponse.getId());

		//Executa o teste de delete
		this.webTestClient.delete().uri("/cartoesutils-log-requisicao-api/log-requisicao-api/delete/" + dtoResponse.getId())
		.header("token-mandatory-auth", "pass")
		.exchange()
		.expectStatus()
		.isNoContent();

		//Faz a busca novamente pelo id esperando um 404 confirmando o sucesso da deleção
		this.webTestClient.get().uri("/cartoesutils-log-requisicao-api/log-requisicao-api/get/" + dtoResponse.getId())
		.accept(MediaType.APPLICATION_JSON)
		.exchange()
		.expectStatus()
		.isNotFound();

	}

	@Test
	public void testGetAll() throws Exception {
		// Limpa a base para o teste
		this.repository.deleteAll().block();

		// Insere 3 registros na base
		LogRequisicaoApiRequestDTO dtoRequest = this.createRequestDTO();
		for(int i = 0; i < 3;  i++) {
			this.repository.save(this.map.dtoParaEntity(dtoRequest)).block();
		}

		//Valida a busca por todos os registros inseridos, validando se há 3 registros inseridos e se o tipo é o correspondente.
		this.webTestClient.get().uri("/cartoesutils-log-requisicao-api/log-requisicao-api/get/all")
		.accept(MediaType.APPLICATION_JSON)
		.exchange()
		.expectStatus()
		.isOk()
		.expectBodyList(LogRequisicaoApiResponseDTO.class)
		.hasSize(3);

	}

	@Test
	public void testGetAllEventStream() {
		// Limpa a base para o teste
		this.repository.deleteAll().block();

		// Insere 3 registros na base
		LogRequisicaoApiRequestDTO dtoRequest = this.createRequestDTO();
		for (int i = 0; i < 2; i++) {
			this.repository.save(this.map.dtoParaEntity(dtoRequest)).block();
		}

		//Executa o event-stream e guarda o resultado, validando codigo de retorno e classe de retorno
		FluxExchangeResult<LogRequisicaoApiResponseDTO> result = this.webTestClient.get().uri("/cartoesutils-log-requisicao-api/log-requisicao-api/get/all/event-stream")
				.accept(MediaType.APPLICATION_STREAM_JSON)
				.exchange()
				.expectStatus().isOk()
				.returnResult(LogRequisicaoApiResponseDTO.class);

		//Valida a integridade do event
		Flux<LogRequisicaoApiResponseDTO> eventFlux = result.getResponseBody();

		StepVerifier.create(eventFlux)
		.expectNextCount(2)
		.thenCancel()
		.verify();

	}

	@Test
	public void testSave() throws Exception {
		// Limpa a base para o teste
		this.repository.deleteAll().block();

		// Cria o objeto e insere na base, verificando no retorno se o objeto criado é o mesmo enviado.
		LogRequisicaoApiRequestDTO dtoRequest = this.createRequestDTO();
		byte[] responseBody = this.webTestClient.post().uri("/cartoesutils-log-requisicao-api/log-requisicao-api/save")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.body(Mono.just(dtoRequest), LogRequisicaoApiRequestDTO.class)
				.exchange()
				.expectStatus()
				.isCreated()
				.expectBody()
				.jsonPath("$.url").isEqualTo(URL_TEST_API)
				.jsonPath("$.aplicacao").isEqualTo(APLICACAO)
				.jsonPath("$.metodo").isEqualTo(GET)
				.jsonPath("$.codRetorno").isEqualTo(_200).returnResult().getResponseBody();
		LogRequisicaoApiResponseTestModel dtoResponse = jsonAsDto(responseBody);

		//Recupera o objeto da base pelo id, verificando assim que o mesmo esta persistido
		this.webTestClient.get().uri("/cartoesutils-log-requisicao-api/log-requisicao-api/get/" + dtoResponse.getId())
		.accept(MediaType.APPLICATION_JSON)
		.exchange()
		.expectStatus()
		.isOk()
		.expectBody()
		.jsonPath("$.id").isEqualTo(dtoResponse.getId());
	}

	@Test
	public void getNotCode200() {
		// Limpa a base para o teste
		this.repository.deleteAll().block();

		//Cria e salva um objeto com codigo 400 e outro com 200
		LogRequisicaoApiRequestDTO dtoRequest400 = this.createRequestDTO();
		dtoRequest400.codRetorno(Integer.toString(HttpStatus.BAD_REQUEST.value()));
		this.repository.save(this.map.dtoParaEntity(dtoRequest400)).block();

		LogRequisicaoApiRequestDTO dtoRequest200 = this.createRequestDTO();
		this.repository.save(this.map.dtoParaEntity(dtoRequest200)).block();

		//Valida a busca por todos os registros inseridos, validando se há apenas 1 com código diferente de 200 e não mais.
		this.webTestClient.get().uri("/cartoesutils-log-requisicao-api/log-requisicao-api/get/not-code-200")
		.accept(MediaType.APPLICATION_JSON)
		.exchange()
		.expectStatus()
		.isOk()
		.expectBodyList(LogRequisicaoApiResponseDTO.class)
		.hasSize(1);
	}

	@Test
	public void deleteAll() {
		// Insere 3 registros na base
		LogRequisicaoApiRequestDTO dtoRequest = this.createRequestDTO();
		for (int i = 0; i < 2; i++) {
			this.repository.save(this.map.dtoParaEntity(dtoRequest)).block();
		}

		//Executa o teste de delete devendo retornar 204
		this.webTestClient.delete().uri("/cartoesutils-log-requisicao-api/log-requisicao-api/delete/all")
		.header("token-mandatory-auth", "pass")
		.exchange()
		.expectStatus()
		.isNoContent();

		//Confirma o resultado de sucesso do teste buscando elementos na base esperando 404.
		this.webTestClient.get().uri("/cartoesutils-log-requisicao-api/log-requisicao-api/get/all")
		.accept(MediaType.APPLICATION_JSON)
		.exchange()
		.expectStatus()
		.isNotFound();
	}

	private LogRequisicaoApiRequestDTO createRequestDTO() {
		LogRequisicaoApiRequestDTO dtoRequest = LogRequisicaoApiRequestDTO.builder()
				.aplicacao(APLICACAO)
				.bodyEnvio(BODY_ENVIO)
				.bodyRetorno(BODY_RETORNO)
				.codRetorno(_200)
				.metodo(GET)
				.url(URL_TEST_API);
		return dtoRequest;

	}

	public static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static LogRequisicaoApiResponseTestModel jsonAsDto(byte[] json) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.readValue(json, LogRequisicaoApiResponseTestModel.class);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
