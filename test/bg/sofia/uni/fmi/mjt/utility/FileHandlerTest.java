package bg.sofia.uni.fmi.mjt.utility;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileHandlerTest {

    @TempDir
    Path tempDir;

    @Test
    void testCreateDirectoryIfAbsentCreatesDirectory() {
        Path file = tempDir.resolve("nested/dir/test.txt");
        assertFalse(Files.exists(file.getParent()),
            "Expected to not have dir created");

        FileHandler.createDirectoryIfAbsent(file);

        assertTrue(Files.exists(file.getParent()),
            "Expected to dir created");

    }

    @Test
    void testCreateDirectoryThrowsUncheckedIO() throws IOException {
        Path fakeDir = tempDir.resolve("not-a-dir.txt");
        Files.createFile(fakeDir);
        Path invalid = fakeDir.resolve("child/another");

        assertThrows(UncheckedIOException.class,
            () -> FileHandler.createDirectoryIfAbsent(invalid),
            "Expected to wrap IOException when invalid path file/dir");
    }

    @Test
    void testCreateFileIfAbsentCreatesFile() {
        Path file = tempDir.resolve("test.txt");
        assertFalse(Files.exists(file),
            "Expected the file to not have be created");
        FileHandler.createFileIfAbsent(file);

        assertTrue(Files.exists(file),
            "Expected the file to be created");

    }

    @Test
    void testCreateFileThrowsUncheckedIO() {
        Path file = tempDir.resolve("dir", "test");
        assertThrows(UncheckedIOException.class,
            () ->         FileHandler.createFileIfAbsent(file),
            "Expected to throw an exception because no file present");

    }

    @Test
    void testCheckFileIsEmptyReturnTrueOnEmpty() {
        Path file = tempDir.resolve("empty.txt");
        FileHandler.createFileIfAbsent(file);
        assertTrue(FileHandler.checkFileIsEmpty(file),
            "Expected the file to be empty but was not");
    }

    @Test
    void testCheckFileIsEmptyThrowsUncheckedIO() {
        Path file = tempDir.resolve("test.txt");

        assertThrows(UncheckedIOException.class,
            () -> FileHandler.checkFileIsEmpty(file),
            "Expected to throw an exception because no file present");
    }

    @Test
    void testInitializeReaderReadsContent() throws Exception {
        Path file = Files.createTempFile("readerContent", ".txt");
        Files.writeString(file, "hello");

        Reader reader = FileHandler.intializeReader(file);

        char[] buffer = new char[5];
        reader.read(buffer);

        assertEquals("hello", new String(buffer), "Expected the string read to match");

        reader.close();
    }

    @Test
    void testInitializeReaderThrowsUncheckedIOException() throws IOException {
        Path dir = Files.createTempDirectory("readerFail");

        assertThrows(UncheckedIOException.class,
                () -> FileHandler.intializeReader(dir), "Expected the reader to throw excetpion when no file in path");
    }

    @Test
    void testInitializeWriterWritesContent() throws Exception {
        Path file = Files.createTempFile("writerTest", ".txt");

        Writer writer = FileHandler.intializeWriter(file);
        writer.write("data");
        writer.close();

        String content = Files.readString(file);
        assertEquals("data", content, "Expected the written content to match");
    }

    @Test
    void testInitializeWriterThrowsUncheckedIOException() throws IOException {
        Path dir = Files.createTempDirectory("writerFail");

        assertThrows(UncheckedIOException.class,
                () -> FileHandler.intializeWriter(dir), "Expected an exception to be thrown when no files in dir");
    }

}
