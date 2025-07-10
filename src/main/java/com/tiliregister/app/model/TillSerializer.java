package com.tiliregister.app.model;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class TillSerializer extends JsonSerializer<Till> {

    @Override
    public void serialize(Till till, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeNumberField("id", till.getId());
        gen.writeStringField("name", till.getName());
        gen.writeEndObject();
    }
}
