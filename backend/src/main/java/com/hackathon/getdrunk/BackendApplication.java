package com.hackathon.getdrunk;

import static springfox.documentation.builders.PathSelectors.regex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableWebMvc
@EnableSwagger2
public class BackendApplication extends WebMvcConfigurerAdapter {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		if (!registry.hasMappingForPattern("/**")) {
			registry.addResourceHandler("/**").addResourceLocations(
					"classpath:/WEB-INF/");
		}
	}

	@Bean
	public Docket newsApi() {
		return new Docket(DocumentationType.SWAGGER_2).groupName("api")
				.apiInfo(apiInfo()).select().paths(regex("/api.*")).build();
	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder()
				.title("Drink 4 Fit")
				.description("API")
				.termsOfServiceUrl(
						"http://www-03.ibm.com/software/sla/sladb.nsf/sla/bm?Open")
				.contact("Drink4Fit-Team")
				.license("Apache License Version 2.0")
				.licenseUrl(
						"https://github.com/IBM-Bluemix/news-aggregator/blob/master/LICENSE")
				.version("2.0").build();
	}

}
