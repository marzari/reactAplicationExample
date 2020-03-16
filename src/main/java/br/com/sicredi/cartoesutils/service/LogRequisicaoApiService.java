package br.com.sicredi.cartoesutils.service;

import org.bson.types.ObjectId;

import br.com.sicredi.cartoesutils.dto.LogRequisicaoApiRequestDTO;
import br.com.sicredi.cartoesutils.dto.LogRequisicaoApiResponseDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface LogRequisicaoApiService {

	Flux<LogRequisicaoApiResponseDTO> findAll();

	Mono<LogRequisicaoApiResponseDTO> findById(ObjectId id);

	Mono<LogRequisicaoApiResponseDTO> save(LogRequisicaoApiRequestDTO log);

	Mono<Void> delete(ObjectId id);

	Mono<Void> deleteAll();

	Flux<LogRequisicaoApiResponseDTO> getNotCode200();

}
