package com.inventary.Deserializer.Movements;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.inventary.Model.Movements.Supplier;
import com.inventary.Repository.Movements.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SupplierDeserializer extends JsonDeserializer<Supplier> {

    @Autowired
    private SupplierRepository supplierRepository;

    @Override
    public Supplier deserialize(JsonParser p, DeserializationContext context) throws IOException, JsonProcessingException {
        Long id = p.getLongValue();
        return supplierRepository.findById(id).orElseThrow(() -> new RuntimeException("Invalid Supplier id: " + id));
    }
}
