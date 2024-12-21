package com.inventary.Deserializer.Authentication;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.inventary.Model.Authentication.Security.Role;
import com.inventary.Repository.Authentication.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RoleDeserializer extends JsonDeserializer<Role> {
    @Autowired
    private RoleRepository roleRepository;

    @Override
    public Role deserialize(JsonParser p, DeserializationContext context) throws IOException, JsonProcessingException {
        Long id = p.getLongValue();
        return roleRepository.findById(id).orElseThrow(() -> new RuntimeException("Invalid Role id: " + id));
    }
}
