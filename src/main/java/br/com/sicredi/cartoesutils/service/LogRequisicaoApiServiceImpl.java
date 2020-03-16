package br.com.sicredi.cartoesutils.service;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.sicredi.cartoesutils.dto.LogRequisicaoApiRequestDTO;
import br.com.sicredi.cartoesutils.dto.LogRequisicaoApiResponseDTO;
import br.com.sicredi.cartoesutils.exception.LogApiNotFoudException;
import br.com.sicredi.cartoesutils.mapper.MapperUtil;
import br.com.sicredi.cartoesutils.model.LogRequisicaoApi;
import br.com.sicredi.cartoesutils.repository.LogRequisicaoApiRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class LogRequisicaoApiServiceImpl implements LogRequisicaoApiService {

	private MapperUtil mapper;
	private LogRequisicaoApiRepository repository;

	@Autowired
	public LogRequisicaoApiServiceImpl(MapperUtil mapper, LogRequisicaoApiRepository repository) {
		super();
		this.mapper = mapper;
		this.repository = repository;
	}

	@Override
	public Flux<LogRequisicaoApiResponseDTO> findAll() {
		return this.mapper.mapFluxEntityToDTO(this.repository.findFirst20ByDtAcessoNotNullOrderByDtAcessoDesc());
	}

	@Override
	public Mono<LogRequisicaoApiResponseDTO> findById(final ObjectId id) {
		Mono<LogRequisicaoApi> monoLogReqApi = this.repository.findById(id);
		if (monoLogReqApi.block() == null) {
			throw new LogApiNotFoudException("Registro não encontrado.");
		}
		return this.mapper.mapMonoEntityToDTO(this.repository.findById(id));
	}

	@Override
	public Mono<LogRequisicaoApiResponseDTO> save(final LogRequisicaoApiRequestDTO dto) {
		LogRequisicaoApi entity = this.mapper.dtoParaEntity(dto);
		Mono<LogRequisicaoApi> saved = this.repository.save(entity);
		return this.mapper.mapMonoEntityToDTO(saved);
	}

	@Override
	public Mono<Void> delete(final ObjectId id) {
		return this.repository.deleteById(id);
	}

	@Override
	public Mono<Void> deleteAll() {
		return this.repository.deleteAll();
	}

	@Override
	public Flux<LogRequisicaoApiResponseDTO> getNotCode200() {
		return this.mapper.mapFluxEntityToDTO(this.repository.findFirst20ByCodRetornoNotOrderByDtAcessoDesc("200"));
	}

}
