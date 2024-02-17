package ru.flamexander.reactive.service.integrations;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.flamexander.reactive.service.dtos.ProductDetailsDto;
import ru.flamexander.reactive.service.exceptions.AppException;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeoutException;

@Component
@RequiredArgsConstructor
public class ProductDetailsServiceIntegration {
    private static final Logger logger = LoggerFactory.getLogger(ProductDetailsServiceIntegration.class.getName());
    private static final String INTEGRATION_ERROR = "PRODUCT_DETAILS_SERVICE_INTEGRATION_ERROR";
    private static final String TIMEOUT_ERROR = "PRODUCT_DETAILS_SERVICE_TIMEOUT_ERROR";
    private static final String TIMEOUT_ERROR_LOG_MESSAGE = "Timeout while fetching product details";
    private static final String FORMAT_SPECIFICATORS = "{}: {}";

    private final WebClient productDetailsServiceWebClient;

    public Mono<ProductDetailsDto> getProductDetailsById(Long id) {
        logger.info("SEND REQUEST FOR PRODUCT_DETAILS-ID: {}", id);
        return productDetailsServiceWebClient.get()
                .uri("/api/v1/details/{id}", id)
                .retrieve()
                .onStatus(
                        HttpStatus::isError,
                        clientResponse -> Mono.error(new AppException(INTEGRATION_ERROR))
                )
                .bodyToMono(ProductDetailsDto.class)
                .timeout(Duration.ofSeconds(4))
                .onErrorResume(TimeoutException.class, e -> {
                    logger.error(FORMAT_SPECIFICATORS, TIMEOUT_ERROR_LOG_MESSAGE, e.getMessage());
                    return Mono.error(new AppException(TIMEOUT_ERROR));
                })
                .log();
    }

    public Flux<ProductDetailsDto> getProductDetailsByIds(List<Long> ids) {
        logger.info("SEND REQUEST FOR PRODUCT_DETAILS-IDS: {}", ids);
        return productDetailsServiceWebClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/details/list/by_ids")
                        .queryParam("ids", ids)
                        .build())
                .retrieve()
                .onStatus(
                        HttpStatus::isError,
                        clientResponse -> Mono.error(new AppException(INTEGRATION_ERROR))
                )
                .bodyToFlux(ProductDetailsDto.class)
                .timeout(Duration.ofSeconds(4))
                .onErrorResume(TimeoutException.class, e -> {
                    logger.error(FORMAT_SPECIFICATORS, TIMEOUT_ERROR_LOG_MESSAGE, e.getMessage());
                    return Flux.error(new AppException(TIMEOUT_ERROR));
                })
                .log();
    }
}
