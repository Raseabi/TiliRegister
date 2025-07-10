package com.tiliregister.app.model;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class TillFunctionSerializer extends JsonSerializer<TillFunction> {

    @Override
    public void serialize(TillFunction tillFunction, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeNumberField("id", tillFunction.getId());
        gen.writeStringField("name", tillFunction.getName());
        gen.writeEndObject();
    }
}
