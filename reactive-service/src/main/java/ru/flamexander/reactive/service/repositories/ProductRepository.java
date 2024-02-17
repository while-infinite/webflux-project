package ru.flamexander.reactive.service.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import ru.flamexander.reactive.service.entities.Product;

import java.util.List;

@Repository
public interface ProductRepository extends ReactiveCrudRepository<Product, Long> {
     Flux<Product> findAllByIdIn(List<Long> ids);
}
