package com.inventary.Deserializer.Products;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.inventary.Model.Products.ProductCategory;
import com.inventary.Repository.Products.ProductCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ProductCategoryDeserializer extends JsonDeserializer<ProductCategory> {
    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Override
    public ProductCategory deserialize(JsonParser p, DeserializationContext context) throws IOException, JsonProcessingException {
        Long id = p.getLongValue();
        return productCategoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Invalid Product Category id: " + id));
    }
}
