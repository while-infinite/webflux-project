package ru.flamexander.product.details.service.controllers;

import org.springframework.web.bind.annotation.*;
import ru.flamexander.product.details.service.dtos.ProductDetailsDto;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/details")
public class ProductDetailsController {
    @GetMapping("/{id}")
    public ProductDetailsDto getProductDetailsById(@PathVariable Long id) throws InterruptedException {
        if (id % 2 == 0)
            return null;
        Thread.sleep(2500 + (int) (Math.random() * 2500));
        return new ProductDetailsDto(id, "Product description..");
    }

    @GetMapping("/list/by_ids")
    public List<ProductDetailsDto> getProductDetailsByIds(@RequestParam List<Long> ids) throws InterruptedException {
        Thread.sleep(2500 + (int) (Math.random() * 2500));
        return ids.stream().map(id -> {
                    if (id % 2 == 0)
                        return null;
                    return new ProductDetailsDto(id, "Product description.." + id);
                })
                .collect(Collectors.toList());
    }
}
