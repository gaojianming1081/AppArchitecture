package com.lib.network.convert;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * 网络响应体转换类
 */
public final class NetWorkResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private static final String TAG = NetWorkResponseBodyConverter.class.getSimpleName();
    private final Gson gson;
    private final TypeAdapter<T> adapter;

    public NetWorkResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }

    @Override
    public T convert(ResponseBody value) {
        JsonReader jsonReader = gson.newJsonReader(value.charStream());

        try {
            T result = adapter.read(jsonReader);

            if (jsonReader.peek() != JsonToken.END_DOCUMENT) {
                String error = "JSON document was not fully consumed.";
                Log.e(TAG, error);
                throw new JsonIOException(error);
            }

            return result;
        } catch (Exception e) {
            Log.e(TAG, "LJCResponseBodyConverter Parse JSON error", e);
            throw new JsonParseException(e);
        } finally {
            value.close();
        }
    }
}