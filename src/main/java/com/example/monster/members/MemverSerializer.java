package com.example.monster.members;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class MemverSerializer extends JsonSerializer<Member> {

    @Override
    public void serialize(Member value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeNumberField("id", value.getId());
        gen.writeStringField("username", value.getUsername());
        gen.writeStringField("nickname", value.getNickname());
        gen.writeEndObject();
    }
}
