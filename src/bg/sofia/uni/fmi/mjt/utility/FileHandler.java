package bg.sofia.uni.fmi.mjt.utility;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileHandler {
    public static void createDirectoryIfAbsent(Path file) {
        if (!Files.exists(file.getParent())) {
            try {
                Files.createDirectories(file.getParent());
            } catch (IOException e) {
                throw new UncheckedIOException("Problem on the server when accessing a" +
                    " directory contact support", e);
            }
        }
    }

    public static void createFileIfAbsent(Path file) {
        if (!Files.exists(file)) {
            try {
                Files.createFile(file);
            } catch (IOException e) {
                throw new UncheckedIOException("Server aborted because of a file error", e);
            }
        }
    }

    public static boolean checkFileIsEmpty(Path file) {
        try {
            if (Files.size(file) == 0) {
                return true; // nothing to read
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Server error occurred while checking file size contact support", e);
        }
        return false;
    }

    public static Reader intializeReader(Path file) {
        createDirectoryIfAbsent(file);
        createFileIfAbsent(file);
        try {
            return new FileReader(file.toFile());
        } catch (FileNotFoundException e) {
            throw new UncheckedIOException("Server aborted because of a file error", e);

        }
    }

    public static Writer intializeWriter(Path file) {
        try {
            return new FileWriter(file.toFile());
        } catch (IOException e) {
            throw new UncheckedIOException("Server aborted because of a problem when saving information", e);
        }
    }
}
