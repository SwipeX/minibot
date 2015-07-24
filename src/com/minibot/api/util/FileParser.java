package com.minibot.api.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author Tyler Sedlar
 * @since 5/31/2015
 */
public class FileParser {

    public static List<String> lines(String file) {
        List<String> list = new LinkedList<>();
        Path path = Paths.get(file);
        if (!path.toFile().exists()) {
            return null;
        }
        try (Stream<String> stream = Files.lines(path)) {
            stream.forEachOrdered(list::add);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return list;
    }
}
