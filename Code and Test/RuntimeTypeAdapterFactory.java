import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.google.gson.internal.Streams;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * A custom {@code TypeAdapterFactory} for Gson that enables polymorphic
 * (de)serialization by embedding a type label field into the JSON representation.
 *
 * <p>This factory allows you to serialize and deserialize an abstract class or
 * interface by registering known subtypes along with a unique label for each.
 * When serializing, the label is included in the JSON under a configurable key
 * (e.g., "type"). When deserializing, the label is used to instantiate the correct subtype.</p>
 *
 * <p>This is especially useful for saving collections or references to abstract types,
 * such as {@code InventoryItem}, where the concrete subtype (e.g., {@code FoodItem}
 * or {@code GiftItem}) must be preserved in the save file.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * RuntimeTypeAdapterFactory<InventoryItem> itemFactory = RuntimeTypeAdapterFactory
 *     .of(InventoryItem.class, "type")
 *     .registerSubtype(FoodItem.class, "FoodItem")
 *     .registerSubtype(GiftItem.class, "GiftItem");
 *
 * Gson gson = new GsonBuilder()
 *     .registerTypeAdapterFactory(itemFactory)
 *     .create();
 * }</pre>
 *
 * @param <T> the base type for which subtypes will be registered
 * @author Yu Li
 * @version 1.0
 */

public class RuntimeTypeAdapterFactory<T> implements TypeAdapterFactory {

    private final Class<?> baseType;
    private final String typeFieldName;
    private final Map<String, Class<?>> labelToSubtype = new HashMap<>();
    private final Map<Class<?>, String> subtypeToLabel = new HashMap<>();

    private RuntimeTypeAdapterFactory(Class<?> baseType, String typeFieldName) {
        this.baseType = baseType;
        this.typeFieldName = typeFieldName;
    }

    public static <T> RuntimeTypeAdapterFactory<T> of(Class<T> baseType, String typeFieldName) {
        return new RuntimeTypeAdapterFactory<>(baseType, typeFieldName);
    }

    public RuntimeTypeAdapterFactory<T> registerSubtype(Class<? extends T> type, String label) {
        if (type == null || label == null) {
            throw new NullPointerException("type and label must not be null");
        }
        if (labelToSubtype.containsKey(label) || subtypeToLabel.containsKey(type)) {
            throw new IllegalArgumentException("Duplicate type or label");
        }
        labelToSubtype.put(label, type);
        subtypeToLabel.put(type, label);
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> TypeAdapter<R> create(Gson gson, TypeToken<R> typeToken) {
        if (!baseType.isAssignableFrom(typeToken.getRawType())) {
            return null;
        }

        final Map<String, TypeAdapter<?>> labelToDelegate = new HashMap<>();
        final Map<Class<?>, TypeAdapter<?>> subtypeToDelegate = new HashMap<>();

        for (Map.Entry<String, Class<?>> entry : labelToSubtype.entrySet()) {
            TypeAdapter<?> delegate = gson.getDelegateAdapter(this, TypeToken.get(entry.getValue()));
            labelToDelegate.put(entry.getKey(), delegate);
            subtypeToDelegate.put(entry.getValue(), delegate);
        }

        return new TypeAdapter<R>() {
            @Override
            public void write(JsonWriter out, R value) throws IOException {
                Class<?> srcType = value.getClass();
                String label = subtypeToLabel.get(srcType);
                if (label == null) {
                    throw new JsonParseException("Unregistered subtype: " + srcType.getName());
                }

                TypeAdapter<R> delegate = (TypeAdapter<R>) subtypeToDelegate.get(srcType);
                JsonObject jsonObject = delegate.toJsonTree(value).getAsJsonObject();
                jsonObject.addProperty(typeFieldName, label);
                Streams.write(jsonObject, out);
            }

            @Override
            public R read(JsonReader in) throws IOException {
                JsonElement jsonElement = Streams.parse(in);
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                JsonElement labelElement = jsonObject.get(typeFieldName);

                if (labelElement == null) {
                    throw new JsonParseException("Missing type field: " + typeFieldName);
                }

                String label = labelElement.getAsString();
                Class<?> subtype = labelToSubtype.get(label);
                if (subtype == null) {
                    throw new JsonParseException("Unknown type label: " + label);
                }

                TypeAdapter<?> delegate = labelToDelegate.get(label);
                return (R) delegate.fromJsonTree(jsonObject);
            }
        };
    }
}
