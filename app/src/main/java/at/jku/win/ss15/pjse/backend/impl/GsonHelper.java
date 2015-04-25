package at.jku.win.ss15.pjse.backend.impl;

import com.google.gson.Gson;

import java.io.IOException;

/**
 * Created by Florian on 25.04.2015.
 */
public class GsonHelper {

    private static Gson instance = null;

    private static Gson getInstance() {
        if (instance == null)
            instance = new Gson();
        return instance;
    }

    public static String seralizeObject(Object o) throws IOException {
        return getInstance().toJson(o);
    }

    public static <T> T deseralizeObject(String o, Class<T> tClass) throws IOException, ClassNotFoundException {
        return getInstance().fromJson(o, tClass);
    }
}
