package br.com.sicredi.cartoesutils.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import br.com.sicredi.cartoesutils.model.LogRequisicaoApi;
import reactor.core.publisher.Flux;


public interface LogRequisicaoApiRepository extends ReactiveMongoRepository<LogRequisicaoApi, ObjectId> {

	Flux<LogRequisicaoApi> findFirst20ByDtAcessoNotNullOrderByDtAcessoDesc();

	Flux<LogRequisicaoApi> findFirst20ByCodRetornoNotOrderByDtAcessoDesc(String codRetorno);

}
