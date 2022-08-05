package com.example.monster.common.serializers;

import com.example.monster.account.entity.Account;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class AccountSerializer extends JsonSerializer<Account> {

    @Override
    public void serialize(Account value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeNumberField("id", value.getId());
        gen.writeStringField("username", value.getUsername());
        gen.writeStringField("nickname", value.getNickname());
        gen.writeEndObject();
    }
}
