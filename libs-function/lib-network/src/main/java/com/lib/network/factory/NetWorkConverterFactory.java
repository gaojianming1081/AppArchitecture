package com.lib.network.factory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.lib.network.convert.NetWorkRequestBodyConverter;
import com.lib.network.convert.NetWorkResponseBodyConverter;
import com.lib.network.convert.adapter.DoubleTypeAdapter;
import com.lib.network.convert.adapter.IntTypeAdapter;
import com.lib.network.convert.adapter.LongTypeAdapter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * A {@linkplain Converter.Factory converter} which uses Gson for JSON.
 * <p>
 * Because Gson is so flexible in the types it supports, this converter assumes that it can handle
 * all types. If you are mixing JSON serialization with something else (such as protocol buffers),
 * you must {@linkplain Retrofit.Builder#addConverterFactory(Converter.Factory) add this instance}
 * last to allow the other converters a chance to see their types.
 */
public final class NetWorkConverterFactory extends Converter.Factory {

    private static Gson sGson;

    /**
     * Create an instance using a default {@link Gson} instance for conversion. Encoding to JSON and
     * decoding from JSON (when no charset is specified by a header) will use UTF-8.
     */
    public static NetWorkConverterFactory create() {
//        return create(new Gson());
        return create(buildConverterGson());
    }

    /**
     * Create an instance using {@code gson} for conversion. Encoding to JSON and
     * decoding from JSON (when no charset is specified by a header) will use UTF-8.
     */
    @SuppressWarnings("ConstantConditions") // Guarding public API nullability.
    public static NetWorkConverterFactory create(Gson gson) {
        if (gson == null) throw new NullPointerException("gson == null");
        return new NetWorkConverterFactory(gson);
    }

    private final Gson gson;

    private NetWorkConverterFactory(Gson gson) {
        this.gson = gson;
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations,
                                                            Retrofit retrofit) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new NetWorkResponseBodyConverter<>(gson, adapter);
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type,
                                                          Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new NetWorkRequestBodyConverter<>(gson, adapter);
    }

    private static Gson buildConverterGson() {
        if (sGson == null) {
            sGson = new GsonBuilder()
                    .registerTypeAdapter(Integer.class, new IntTypeAdapter())
                    .registerTypeAdapter(int.class, new IntTypeAdapter())
                    .registerTypeAdapter(Double.class, new DoubleTypeAdapter())
                    .registerTypeAdapter(double.class, new DoubleTypeAdapter())
                    .registerTypeAdapter(Long.class, new LongTypeAdapter())
                    .registerTypeAdapter(long.class, new LongTypeAdapter())
                    .create();
        }
        return sGson;
    }
}
