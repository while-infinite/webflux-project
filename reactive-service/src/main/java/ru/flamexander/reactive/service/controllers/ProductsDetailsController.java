package ru.flamexander.reactive.service.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.flamexander.reactive.service.dtos.DetailedProductDto;
import ru.flamexander.reactive.service.services.ProductDetailsService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/detailed")
@RequiredArgsConstructor
public class ProductsDetailsController {
    private final ProductDetailsService productDetailsService;

    @GetMapping("/{id}")
    public Mono<DetailedProductDto> getProductDetailsById(@PathVariable Long id) {
        return productDetailsService.getProductDetailsById(id);
    }

    @GetMapping("/list/by_ids")
    public Flux<DetailedProductDto> getProductDetailsByIds(@RequestParam List<Long> ids) {
        return productDetailsService.getProductDetailsByIds(ids);
    }

    @GetMapping("/list")
    public Flux<DetailedProductDto> getAllProductDetails() {
        return productDetailsService.getAllProductDetails();
    }

    @GetMapping("/demo")
    public Flux<DetailedProductDto> getManySlowProducts() {
        Mono<DetailedProductDto> p1 = productDetailsService.getProductDetailsById(1L);
        Mono<DetailedProductDto> p2 = productDetailsService.getProductDetailsById(2L);
        Mono<DetailedProductDto> p3 = productDetailsService.getProductDetailsById(3L);
        return p1.mergeWith(p2).mergeWith(p3);
    }
}
