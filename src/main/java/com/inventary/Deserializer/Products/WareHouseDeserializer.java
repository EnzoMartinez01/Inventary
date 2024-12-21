package com.inventary.Deserializer.Products;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.inventary.Model.Products.WareHouse;
import com.inventary.Repository.Products.WareHouseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class WareHouseDeserializer extends JsonDeserializer<WareHouse> {
    @Autowired
    private WareHouseRepository wareHouseRepository;

    @Override
    public WareHouse deserialize(JsonParser p, DeserializationContext context) throws IOException, JsonProcessingException {
        Long id = p.getLongValue();
        return wareHouseRepository.findById(id).orElseThrow(() -> new RuntimeException("Invalid Warehouse id: " + id));
    }
}
