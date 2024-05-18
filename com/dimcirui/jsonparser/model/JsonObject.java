package com.dimcirui.jsonparser.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.dimcirui.jsonparser.exception.*;

/**
 * JsonObject is an unordered collection of key-value pairs.
 * A JsonObject starts with '{' and ends with '}',
 *      and uses ',' to separate key-value pairs.
 * A key must be a string.
 *      Each key is followed by a ':'
 * A value could be a string, a num, true/false, a object or an array.
 *      A value allows these structures to be nested
 * e.g. {"name": "Alice", "age": 23, ["Bob", true]}
 */
public class JsonObject {
    private Map<String, Object> map = new HashMap<>();

    public void put(String key, Object value) {
        map.put(key, value);
    }

    public Object get(String key) {
        return map.get(key);
    }

    public List<Map.Entry<String, Object>> getAllKeyValue() {
        return new ArrayList<>(map.entrySet());
    }

    public JsonObject getJsonObject(String key) {
        return getType(key, JsonObject.class);
    }

    public JsonArray getJsonArray(String key) {
        return getType(key, JsonArray.class);
    }

    private <T> T getType(String key, Class<T> type) {
        if (!map.containsKey(key)) throw new JsonTypeException("Invalid key:" + key);

        Object obj = map.get(key);
        if (!type.isInstance(obj)) throw new JsonTypeException("Value associated with key " + key + " is not of type " + type.getSimpleName());

        return type.cast(obj);
    }
}
