package com.lib.network.convert.adapter;

import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 *
 */
public class DoubleTypeAdapter extends TypeAdapter<Double> {
    @Override
    public void write(JsonWriter out, Double value) throws IOException {
        out.value(value);
    }

    @Override
    public Double read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return 0.00;
        }
        try {
            String result = in.nextString();
            if ("".equals(result)) {
                return 0.00;
            }
            return Double.parseDouble(result);
        } catch (NumberFormatException e) {
            throw new JsonSyntaxException(e);
        }
    }
}
