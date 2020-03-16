package br.com.sicredi.cartoesutils.mapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.modelmapper.ModelMapper;

import br.com.sicredi.cartoesutils.dto.LogRequisicaoApiRequestDTO;
import br.com.sicredi.cartoesutils.dto.LogRequisicaoApiResponseDTO;
import br.com.sicredi.cartoesutils.model.LogRequisicaoApi;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class MapperUtilTest {

	private ModelMapper mapper;
	private LogRequisicaoApiRequestDTO dtoRequest;
	private MapperUtil mapperUtil;
	private LogRequisicaoApi entity;
	private LocalDateTime now;

	@Before
	public void setUp() throws Exception {
		this.mapper = new ModelMapper();
		this.mapperUtil = new MapperUtil(this.mapper);
		this.dtoRequest = LogRequisicaoApiRequestDTO.builder()
				.aplicacao("test-api")
				.bodyEnvio("test-body-envio")
				.bodyRetorno("test-body-retorno")
				.codRetorno("200")
				.metodo("GET")
				.url("url:/test/api");
		this.entity = new LogRequisicaoApi();
		this.entity.setAplicacao("test-api");
		this.entity.setBodyEnvio("test-body-envio");
		this.entity.setBodyRetorno("test-body-retorno");
		this.entity.setCodRetorno("200");
		this.entity.setMetodo("GET");
		this.entity.setUrl("url:/test/api");
		this.entity.setId(new ObjectId("5e5e8eb5e589f642e864bcbc"));
		this.now = this.entity.getDtAcesso();
	}

	@Test
	public void testDtoParaEntity() {
		LogRequisicaoApi entity = this.mapperUtil.dtoParaEntity(this.dtoRequest);
		assertEquals("test-api", entity.getAplicacao());
		assertEquals("test-body-envio", entity.getBodyEnvio());
		assertEquals("test-body-retorno", entity.getBodyRetorno());
		assertEquals("200", entity.getCodRetorno());
		assertEquals("GET", entity.getMetodo());
		assertEquals("url:/test/api", entity.getUrl());

	}

	@Test
	public void testEntityParaDto() {
		LogRequisicaoApiResponseDTO dto = this.mapperUtil.entityParaDto(this.entity);
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
	public void testMapListEntitiesToDto() {
		List<LogRequisicaoApi> listEntities = new ArrayList<LogRequisicaoApi>();
		listEntities.add(this.entity);
		List<LogRequisicaoApiResponseDTO> listDto = this.mapperUtil.mapListEntitiesToDto(listEntities);
		for (LogRequisicaoApiResponseDTO dto : listDto) {
			assertEquals("test-api", dto.getAplicacao());
			assertEquals("test-body-envio", dto.getBodyEnvio());
			assertEquals("test-body-retorno", dto.getBodyRetorno());
			assertEquals("200", dto.getCodRetorno());
			assertEquals("GET", dto.getMetodo());
			assertEquals("url:/test/api", dto.getUrl());
			assertEquals("5e5e8eb5e589f642e864bcbc", dto.getId());
			assertEquals(this.now, dto.getDtAcesso());
		}
	}

	@Test
	public void testMapMonoEntityToDTO() {
		Mono<LogRequisicaoApi> monoEntity = Mono.just(this.entity);
		Mono<LogRequisicaoApiResponseDTO> monoDto = this.mapperUtil.mapMonoEntityToDTO(monoEntity);
		LogRequisicaoApiResponseDTO dto = monoDto.block();
		assertNotNull(dto);
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
	public void testMapFluxEntityToDTO() {
		List<LogRequisicaoApi> listEntities = new ArrayList<LogRequisicaoApi>();
		listEntities.add(this.entity);
		Flux<LogRequisicaoApi> fluxEntity = Flux.fromIterable(listEntities);
		Flux<LogRequisicaoApiResponseDTO> fluxDto = this.mapperUtil.mapFluxEntityToDTO(fluxEntity);

		List<LogRequisicaoApiResponseDTO> listDto = fluxDto.collectList().block();
		for (LogRequisicaoApiResponseDTO dto : listDto) {
			assertEquals("test-api", dto.getAplicacao());
			assertEquals("test-body-envio", dto.getBodyEnvio());
			assertEquals("test-body-retorno", dto.getBodyRetorno());
			assertEquals("200", dto.getCodRetorno());
			assertEquals("GET", dto.getMetodo());
			assertEquals("url:/test/api", dto.getUrl());
			assertEquals("5e5e8eb5e589f642e864bcbc", dto.getId());
			assertEquals(this.now, dto.getDtAcesso());
		}

	}

}
