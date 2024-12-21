package com.inventary.Deserializer.Products;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.inventary.Model.Products.Product;
import com.inventary.Repository.Products.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ProductDeserializer extends JsonDeserializer<Product> {
    @Autowired
    private ProductRepository productRepository;

    @Override
    public Product deserialize(JsonParser p, DeserializationContext context) throws IOException, JsonProcessingException {
        Long id = p.getLongValue();
        return productRepository.findById(id).orElseThrow(() -> new RuntimeException("Invalid Product id: " + id));
    }
}
