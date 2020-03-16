package br.com.sicredi.cartoesutils.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.time.LocalDateTime;

import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;

import br.com.sicredi.cartoesutils.dto.LogRequisicaoApiRequestDTO;
import br.com.sicredi.cartoesutils.dto.LogRequisicaoApiResponseDTO;
import br.com.sicredi.cartoesutils.exception.LogApiNotFoudException;
import br.com.sicredi.cartoesutils.mapper.MapperUtil;
import br.com.sicredi.cartoesutils.model.LogRequisicaoApi;
import br.com.sicredi.cartoesutils.repository.LogRequisicaoApiRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@RunWith(MockitoJUnitRunner.class)
public class LogRequisicaoApiServiceTest {

	@InjectMocks
	private LogRequisicaoApiServiceImpl service;
	@Mock
	private LogRequisicaoApiRepository repository;
	private LogRequisicaoApi entity;
	private LocalDateTime now;

	@Before
	public void setUp() {
		this.entity = new LogRequisicaoApi();
		this.entity.setAplicacao("test-api");
		this.entity.setBodyEnvio("test-body-envio");
		this.entity.setBodyRetorno("test-body-retorno");
		this.entity.setCodRetorno("200");
		this.entity.setMetodo("GET");
		this.entity.setUrl("url:/test/api");
		this.entity.setId(new ObjectId("5e5e8eb5e589f642e864bcbc"));
		this.now = this.entity.getDtAcesso();

		this.service = new LogRequisicaoApiServiceImpl(new MapperUtil(new ModelMapper()), this.repository);
	}

	@Test
	public void testFindAll() {
		Mockito.when(this.repository.findFirst20ByDtAcessoNotNullOrderByDtAcessoDesc())
		.thenReturn(Flux.just(this.entity));
		Flux<LogRequisicaoApiResponseDTO> fluxDto = this.service.findAll();

		StepVerifier.create(fluxDto).expectNextCount(1).verifyComplete();

		fluxDto.collectList().block().forEach(dto -> {
			assertEquals("test-api", dto.getAplicacao());
			assertEquals("test-body-envio", dto.getBodyEnvio());
			assertEquals("test-body-retorno", dto.getBodyRetorno());
			assertEquals("200", dto.getCodRetorno());
			assertEquals("GET", dto.getMetodo());
			assertEquals("url:/test/api", dto.getUrl());
			assertEquals("5e5e8eb5e589f642e864bcbc", dto.getId());
			assertEquals(this.now, dto.getDtAcesso());
		});

	}

	@Test
	public void testFindById() {
		Mockito.when(this.repository.findById(ArgumentMatchers.any(ObjectId.class))).thenReturn(Mono.just(this.entity));

		Mono<LogRequisicaoApiResponseDTO> entity = this.service.findById(new ObjectId("5e5e8eb5e589f642e864bcbc"));
		assertNotNull("Ao possuir uma collection válida, não pode retornar null", entity);
		LogRequisicaoApiResponseDTO dto = entity.block();
		assertEquals("test-api", dto.getAplicacao());
		assertEquals("test-body-envio", dto.getBodyEnvio());
		assertEquals("test-body-retorno", dto.getBodyRetorno());
		assertEquals("200", dto.getCodRetorno());
		assertEquals("GET", dto.getMetodo());
		assertEquals("url:/test/api", dto.getUrl());
		assertEquals("5e5e8eb5e589f642e864bcbc", dto.getId());
		assertEquals(this.now, dto.getDtAcesso());
	}

	@Rule
	public ExpectedException expected = ExpectedException.none();

	@Test
	public void testFindByIdNotFound() {
		this.expected.expect(LogApiNotFoudException.class);
		this.expected.expectMessage("Registro não encontrado.");
		Mockito.when(this.repository.findById(ArgumentMatchers.any(ObjectId.class))).thenReturn(Mono.empty());

		Mono<LogRequisicaoApiResponseDTO> entity = this.service.findById(new ObjectId("5e5e8eb5e589f642e864bcbc"));
		assertNotNull(entity);
	}

	@Test
	public void testSave() {
		Mockito.when(this.repository.save(ArgumentMatchers.any(LogRequisicaoApi.class)))
		.thenReturn(Mono.just(this.entity));
		LogRequisicaoApiRequestDTO dtoRequest = new LogRequisicaoApiRequestDTO();
		dtoRequest.aplicacao("test-api");
		dtoRequest.bodyEnvio("test-body-envio");
		dtoRequest.bodyRetorno("test-body-retorno");
		dtoRequest.codRetorno("200");
		dtoRequest.metodo("GET");
		dtoRequest.url("url:/test/api");

		Mono<LogRequisicaoApiResponseDTO> entity = this.service.save(dtoRequest);
		LogRequisicaoApiResponseDTO dto = entity.block();
		assertEquals("test-api", dto.getAplicacao());
		assertEquals("test-body-envio", dto.getBodyEnvio());
		assertEquals("test-body-retorno", dto.getBodyRetorno());
		assertEquals("200", dto.getCodRetorno());
		assertEquals("GET", dto.getMetodo());
		assertEquals("url:/test/api", dto.getUrl());
		assertEquals("5e5e8eb5e589f642e864bcbc", dto.getId());
		assertEquals(this.now, dto.getDtAcesso());
	}

	@Test
	public void testDelete() {
		Mockito.when(this.repository.deleteById(ArgumentMatchers.any(ObjectId.class))).thenReturn(Mono.empty());
		Mono<Void> mono = this.service.delete(new ObjectId("5e5e8eb5e589f642e864bcbc"));
		assertNotNull(mono);
	}

	@Test
	public void testDeleteAll() {
		Mockito.when(this.repository.deleteAll()).thenReturn(Mono.empty());
		Mono<Void> mono = this.service.deleteAll();
		assertNotNull(mono);
	}

	@Test
	public void testGetNotCode200() {
		Mockito.when(this.repository.findFirst20ByCodRetornoNotOrderByDtAcessoDesc("200")).thenReturn(Flux.just(this.entity));
		this.entity.setCodRetorno("400");

		this.service.getNotCode200().collectList().block().forEach(dto -> {
			assertEquals("test-api", dto.getAplicacao());
			assertEquals("test-body-envio", dto.getBodyEnvio());
			assertEquals("test-body-retorno", dto.getBodyRetorno());
			assertEquals("400", dto.getCodRetorno());
			assertEquals("GET", dto.getMetodo());
			assertEquals("url:/test/api", dto.getUrl());
			assertEquals("5e5e8eb5e589f642e864bcbc", dto.getId());
			assertEquals(this.now, dto.getDtAcesso());
		});

	}

}
