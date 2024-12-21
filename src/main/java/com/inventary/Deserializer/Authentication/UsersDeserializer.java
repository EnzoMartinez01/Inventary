package com.inventary.Deserializer.Authentication;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.inventary.Model.Authentication.Users;
import com.inventary.Repository.Authentication.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class UsersDeserializer extends JsonDeserializer<Users> {
    @Autowired
    private UsersRepository usersRepository;

    @Override
    public Users deserialize(JsonParser p, DeserializationContext context) throws IOException, JsonProcessingException {
        Long id = p.getLongValue();
        return usersRepository.findById(id).orElseThrow(() -> new RuntimeException("Invalid Users id: " + id));
    }
}
