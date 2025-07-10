package com.tiliregister.app.model;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class TiliGroupSerializer  extends JsonSerializer<TiliGroup> {

    @Override
    public void serialize(TiliGroup tiliGroup, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeNumberField("id", tiliGroup.getId());
        gen.writeStringField("name", tiliGroup.getName());
        gen.writeEndObject();
    }
}
