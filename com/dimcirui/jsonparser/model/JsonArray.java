package com.dimcirui.jsonparser.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

import com.dimcirui.jsonparser.exception.*;

/**
 * JsonArray is an ordered collection of values.
 * A JsonArray starts with '[' and ends with ']',
 *      and uses ',' to separate values.
 * A value could be null, a string, a num, a boolean, an object or an array.
 *      A value allows these structures to be nested.
 * e.g. ["Bob", true, {"name": "Alice", "age": 23}]
 */
public class JsonArray implements Iterable<Object> {
    private List<Object> list = new ArrayList<>();

    public void add(Object obj) {
        list.add(obj);
    }

    public Object get(int index) {
        return list.get(index);
    }

    public int size() {
        return list.size();
    }

    public JsonObject getJsonObject(int index) {
        return getType(index, JsonObject.class);
    }

    public JsonArray getJsonArray(int index) {
        return getType(index, JsonArray.class);
    }

    private <T> T getType(int index, Class<T> type) {
        Object obj = list.get(index);
        if (!type.isInstance(obj)) throw new JsonTypeException("Element at index " + index + " is not of type " + type.getSimpleName());

        return type.cast(obj);
    }

    public Iterator<Object> iterator() {
        return list.iterator();
    }
}