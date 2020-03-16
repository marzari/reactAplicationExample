package br.com.sicredi.cartoesutils.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerLopApiConfig {

	@Value("${swagger.enable:true}")
	private boolean swaggerEnable;

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(RequestHandlerSelectors.basePackage("br.com.sicredi.cartoesutils.controller"))
				.paths(PathSelectors.any())
				.build()
				.apiInfo(this.getApiInfo())
				.enable(this.swaggerEnable)
				.protocols(new HashSet<>(Arrays.asList("http", "https")));

	}

	private ApiInfo getApiInfo() {
		return new ApiInfo(
				"log-requisicao-api - REST API.",
				"Microserviço para provisionar acesso de leitura e escrita na entidade do MongoDB logrequisicaoapi. Entidade essa responsável por armazenar o log das requisições externas efetuadas pelas api's internas do Sicredi.",
				"1.0.0",
				"TERMS OF SERVICE URL",
				new Contact("Sicredi", "", "houseofcards@confederacaosicredi.onmicrosoft.com"),
				"LICENSE",
				"http://www.sicredi.com.br",
				Collections.emptyList());
	}

}
