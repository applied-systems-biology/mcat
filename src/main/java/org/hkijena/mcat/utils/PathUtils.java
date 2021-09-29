package org.hkijena.mcat.utils;

import ij.Prefs;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PathUtils {
    private PathUtils() {

    }

    public static Path generateTempFile(String prefix, String suffix) {
        try {
            return Files.createTempFile("MCAT" + prefix, suffix);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Path resolveAndMakeSubDirectory(Path directory, String name) {
        return resolveAndMakeSubDirectory(directory, Paths.get(name));
    }

    public static Path resolveAndMakeSubDirectory(Path directory, Path name) {
        Path result = directory.resolve(name);
        if (!Files.exists(result)) {
            try {
                Files.createDirectories(result);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    public static void deleteDirectoryRecursively(Path path) {
        FileVisitor<Path> visitor = new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
//                System.out.println("Delete: " + file.toString());
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
//                System.out.println("Delete: " + file.toString());
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                if (exc != null) {
                    throw exc;
                }
//                System.out.println("Delete: " + dir.toString());
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        };
        try {
            Files.walkFileTree(path, visitor);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Finds a file in the specified folder with given extension
     *
     * @param folder     the path
     * @param extensions Should contain the dot
     * @return null if no file was found
     */
    public static Path findFileByExtensionIn(Path folder, String... extensions) {
        try {
            return Files.list(folder).filter(p -> Files.isRegularFile(p) && Arrays.stream(extensions).anyMatch(e -> p.toString().endsWith(e))).findFirst().orElse(null);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Finds a file in the specified folder with given extension
     *
     * @param folder     the path
     * @param extensions Should contain the dot
     * @return null if no file was found
     */
    public static List<Path> findFilesByExtensionIn(Path folder, String... extensions) {
        try {
            return Files.list(folder).filter(p -> Files.isRegularFile(p) && (extensions.length == 0 || Arrays.stream(extensions).anyMatch(e -> p.toString().endsWith(e)))).collect(Collectors.toList());
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    /**
     * Returns the first path that exists
     *
     * @param paths paths
     * @return first path that exists or null
     */
    public static Path findAnyOf(Path... paths) {
        for (Path path : paths) {
            if (Files.exists(path)) {
                return path;
            }
        }
        return null;
    }

    /**
     * Converts relative paths to absolute paths, relative to the ImageJ directory
     * Absolute paths are left unchanged
     *
     * @param path the path
     * @return absolute paths
     */
    public static Path relativeToImageJToAbsolute(Path path) {
        if (path.isAbsolute())
            return path;
        Path imageJDir = Paths.get(Prefs.getImageJDir());
        return imageJDir.resolve(path);
    }
}
