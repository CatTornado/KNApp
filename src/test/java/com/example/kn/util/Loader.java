package com.example.kn.util;

import com.example.kn.unit.web.RestServiceTests;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Loader {

    public static String loadTestData(String name) throws IOException {
        InputStream is = RestServiceTests.class.getResourceAsStream(name);
        return new String(Objects.requireNonNull(is).readAllBytes(), UTF_8);
    }

}
