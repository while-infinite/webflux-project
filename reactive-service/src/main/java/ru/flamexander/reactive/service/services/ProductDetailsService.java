package ru.flamexander.reactive.service.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.flamexander.reactive.service.dtos.DetailedProductDto;
import ru.flamexander.reactive.service.dtos.ProductDetailsDto;
import ru.flamexander.reactive.service.entities.Product;
import ru.flamexander.reactive.service.integrations.ProductDetailsServiceIntegration;
import ru.flamexander.reactive.service.repositories.ProductRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductDetailsService {
    private final ProductDetailsServiceIntegration productDetailsServiceIntegration;
    private final ProductRepository productRepository;

    public Mono<DetailedProductDto> getProductDetailsById(Long id) {
        Mono<ProductDetailsDto> productDetails = productDetailsServiceIntegration.getProductDetailsById(id);
        Mono<Product> product = productRepository.findById(id);

        return productDetails.zipWith(product)
                .map(tuple -> {
                    ProductDetailsDto productDetailsDto = tuple.getT1();
                    Product productEntity = tuple.getT2();

                    DetailedProductDto detailedProductDto = new DetailedProductDto();
                    detailedProductDto.setId(productEntity.getId());
                    detailedProductDto.setName(productEntity.getName());
                    detailedProductDto.setDescription(productDetailsDto.getDescription());

                    return detailedProductDto;
                });
    }

    public Flux<DetailedProductDto> getProductDetailsByIds(List<Long> ids) {
        Flux<ProductDetailsDto> productDetailsList = productDetailsServiceIntegration.getProductDetailsByIds(ids);
        Flux<Product> productList = productRepository.findAllByIdIn(ids);

        return Flux.zip(productDetailsList, productList)
                .map(tuple -> {
                    ProductDetailsDto productDetailsDto = tuple.getT1();
                    Product productEntity = tuple.getT2();

                    DetailedProductDto detailedProductDto = new DetailedProductDto();
                    detailedProductDto.setId(productEntity.getId());
                    detailedProductDto.setName(productEntity.getName());
                    detailedProductDto.setDescription(productDetailsDto.getDescription());

                    return detailedProductDto;
                })
                .doOnNext(System.out::println);

    }

    public Flux<DetailedProductDto> getAllProductDetails() {
        Flux<Product> productList = productRepository.findAll();

        return productList.collectList().flatMapMany(products -> {
            List<Long> productIds = products.stream()
                    .map(Product::getId)
                    .collect(Collectors.toList());

            Flux<ProductDetailsDto> productDetailsList =
                    productDetailsServiceIntegration.getProductDetailsByIds(productIds);

            return Flux.zip(productDetailsList, Flux.fromIterable(products), (details, product) -> {
                DetailedProductDto detailedProductDto = new DetailedProductDto();
                detailedProductDto.setId(product.getId());
                detailedProductDto.setName(product.getName());
                detailedProductDto.setDescription(details.getDescription());
                return detailedProductDto;
            });
        });
    }


}
