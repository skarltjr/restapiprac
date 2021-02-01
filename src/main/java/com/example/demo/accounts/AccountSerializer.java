package com.example.demo.accounts;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

//이걸 jsonComponent하면 모든 account가 id만 내보낸다 출력할 때 - 필요에 의해서만 사용하기위해
//     @JsonSerialize(using = AccountSerializer.class) 이벤트에서 어카운트를 사용할 때만
public class AccountSerializer extends JsonSerializer<Account> {
    @Override
    public void serialize(Account account, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("id", account.getId());
        jsonGenerator.writeEndObject();
    }
}
