package com.tiliregister.app.model;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;

public class UserReferenceSerializer extends JsonSerializer<User> {
    @Override
    public void serialize(User user, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeNumberField("id", user.getId());
        gen.writeStringField("username", user.getUsername());
        gen.writeStringField("surname", user.getSurname());
        gen.writeStringField("othernames", user.getOthernames());
        gen.writeEndObject();
    }
}
