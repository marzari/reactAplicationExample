package br.com.sicredi.cartoesutils.mapper;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.sicredi.cartoesutils.dto.LogRequisicaoApiRequestDTO;
import br.com.sicredi.cartoesutils.dto.LogRequisicaoApiResponseDTO;
import br.com.sicredi.cartoesutils.model.LogRequisicaoApi;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class MapperUtil {

	private ModelMapper mapper;

	@Autowired
	public MapperUtil(ModelMapper mapper) {
		this.mapper = mapper;
	}

	public LogRequisicaoApi dtoParaEntity(LogRequisicaoApiRequestDTO dto) {
		return this.mapper.map(dto, LogRequisicaoApi.class);
	}

	public LogRequisicaoApiResponseDTO entityParaDto(LogRequisicaoApi model) {
		return this.mapper.map(model, LogRequisicaoApiResponseDTO.class);
	}

	public List<LogRequisicaoApiResponseDTO> mapListEntitiesToDto(List<LogRequisicaoApi> models) {
		List<LogRequisicaoApiResponseDTO> dtoList = new ArrayList<>(models.size());
		for (LogRequisicaoApi model : models) {
			dtoList.add(this.entityParaDto(model));
		}
		return dtoList;
	}

	public Mono<LogRequisicaoApiResponseDTO> mapMonoEntityToDTO(Mono<LogRequisicaoApi> model) {
		return Mono.just(this.entityParaDto(model.block()));
	}

	public Flux<LogRequisicaoApiResponseDTO> mapFluxEntityToDTO(Flux<LogRequisicaoApi> models) {
		return Flux.fromIterable(this.mapListEntitiesToDto(models.collectList().block()));
	}



}
