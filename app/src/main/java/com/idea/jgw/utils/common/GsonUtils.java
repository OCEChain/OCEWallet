package com.idea.jgw.utils.common;

import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

/**
 * Created by idea on 2018/6/8.
 */

public class GsonUtils {

    public static <T> T parseJson(String json, Type type) {
        Gson gson = new Gson();
        T info = gson.fromJson(gson.toJson(json), type);
        return info;
    }

    public static <T> List<T> jsonToList(String json, Class<? extends T[]> clazz) {

        Gson gson = new Gson();
        T[] array = gson.fromJson(gson.toJson(json), clazz);
        return Arrays.asList(array);

    }
}
