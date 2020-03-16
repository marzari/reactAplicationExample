package br.com.sicredi.cartoesutils.controller;

import java.time.Duration;

import javax.validation.Valid;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.sicredi.cartoesutils.dto.LogRequisicaoApiRequestDTO;
import br.com.sicredi.cartoesutils.dto.LogRequisicaoApiResponseDTO;
import br.com.sicredi.cartoesutils.exception.LogApiNotFoudException;
import br.com.sicredi.cartoesutils.exception.LogApiUnauthorizedException;
import br.com.sicredi.cartoesutils.service.LogRequisicaoApiService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Tiago Marzari
 * @since 27/02/2020
 *
 */

@Slf4j
@RestController
@RequestMapping(value = "/log-requisicao-api")
public class LogRequisicaoApiController {

	@Autowired
	private LogRequisicaoApiService service;

	@Value("${token_mandatory_auth:pass}")
	private String tokenMandatoryAuth;

	@ApiOperation(value = "Exclui um único document pelo seu ID. Necessário token de autorização, incluir no header da requisição o header-param: token-mandatory-auth.")
	@ApiResponses(value = {
			@ApiResponse(code = 204, message = "Objeto processado com sucesso."),
			@ApiResponse(code = 400, message = "Formato do ID inválido: apenas formato hexadecimal."),
			@ApiResponse(code = 401, message = "Token de autorização inválido. Acesso negado.")
	})
	@DeleteMapping(value = "/delete/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public Mono<Void> deleteById(@RequestHeader(value = "token-mandatory-auth", required = true) String token,
			@PathVariable String id) {
		if (this.tokenMandatoryAuth.equals(token)) {
			log.info("Iniciando processo exclusão de coleção.");
			Mono<Void> mono = this.service.delete(new ObjectId(id));
			log.info("Processamento finalizado!");
			return mono;
		} else {
			throw new LogApiUnauthorizedException("Token de autorização inválido. Acesso negado.");
		}
	}

	@ApiOperation(value = "Faz o expurgo de toda a base. Necessário token de autorização, incluir no header da requisição o header-param: token-mandatory-auth.")
	@ApiResponses(value = {
			@ApiResponse(code = 204, message = "Objeto processado com sucesso."),
			@ApiResponse(code = 400, message = "Formato do ID inválido: apenas formato hexadecimal."),
			@ApiResponse(code = 401, message = "Token de autorização inválido. Acesso negado.")
	})
	@DeleteMapping(value = "/delete/all")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public Mono<Void> deleteAll(@RequestHeader(value = "token-mandatory-auth", required = true) String token) {
		if (this.tokenMandatoryAuth.equals(token)) {
			Mono<Void> mono = this.service.deleteAll();
			log.info("Coleções limpas com sucesso!");
			return mono;
		} else {
			throw new LogApiUnauthorizedException("Token de autorização inválido. Acesso negado.");
		}
	}

	@ApiOperation(value = "Busca uma unica entidade pelo seu ID.", response = LogRequisicaoApiResponseDTO.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Registro encontrado.", response = LogRequisicaoApiResponseDTO.class),
			@ApiResponse(code = 400, message = "Formato do ID inválido: apenas formato hexadecimal."),
			@ApiResponse(code = 404, message = "Registro não encontrado.") })
	@GetMapping("/get/{id}")
	@ResponseStatus(HttpStatus.OK)
	public Mono<LogRequisicaoApiResponseDTO> getById(@PathVariable String id) {
		return this.service.findById(new ObjectId(id));
	}

	@ApiOperation(value = "Busca os últimos 20 registros da base, ordenando por ordem decrescente de data.", response = LogRequisicaoApiResponseDTO.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Registro encontrado.", response = LogRequisicaoApiResponseDTO.class),
			@ApiResponse(code = 404, message = "Não há registros inseridos.") })
	@GetMapping("/get/all")
	@ResponseStatus(HttpStatus.OK)
	public Flux<LogRequisicaoApiResponseDTO> getAll() {
		return this.service.findAll()
				.switchIfEmpty(Mono.error(new LogApiNotFoudException("Não há registros inseridos.")));
	}

	@ApiOperation(value = "Stream de objetos json, enviados um a um a cada 3s. O stream finaliza ao chegar no vigésimo elemento. Ordenação decrescente de data.", response = LogRequisicaoApiResponseDTO.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "'Registro encontrado'. Ou 'Não foram encontrados registros' quando o stream estiver vazio.", response = LogRequisicaoApiResponseDTO.class)
	})
	@GetMapping(path = "/get/all/event-stream", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
	@ResponseStatus(HttpStatus.OK)
	public Flux<LogRequisicaoApiResponseDTO> getAllEventStream() {
		return this.service.findAll()
				.switchIfEmpty(Mono.error(new LogApiNotFoudException("Não foram encontrados registros.")))
				.delayElements(Duration.ofMillis(3000));
	}

	@ApiOperation(value = "Busca os últimos 20 registros da base, com código de retorno diferente de 200, ordenando por ordem decrescente de data.", response = LogRequisicaoApiResponseDTO.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Registro encontrado.", response = LogRequisicaoApiResponseDTO.class),
			@ApiResponse(code = 404, message = "Não foram encontrados registros.") })
	@GetMapping("/get/not-code-200")
	@ResponseStatus(HttpStatus.OK)
	public Flux<LogRequisicaoApiResponseDTO> getNotCode200() {
		return this.service.getNotCode200()
				.switchIfEmpty(Mono.error(new LogApiNotFoudException("Não foram encontrados registros.")));
	}

	@ApiOperation(value = "Persiste um objeto logrequisicaoapi na base. Campos url, metodo, codRetorno e aplicacao, são obrigatórios. ", response = LogRequisicaoApiResponseDTO.class)
	@ApiResponses(value = {
			@ApiResponse(code = 201, message = "Created", response = LogRequisicaoApiResponseDTO.class),
			@ApiResponse(code = 400, message = "Requisição possui campos inválidos.") })
	@PostMapping("/save")
	@ResponseStatus(HttpStatus.CREATED)
	public Mono<LogRequisicaoApiResponseDTO> save(@Valid @RequestBody LogRequisicaoApiRequestDTO dto) {
		log.info("Iniciando persistencia de dados.");
		return this.service.save(dto);
	}

}
