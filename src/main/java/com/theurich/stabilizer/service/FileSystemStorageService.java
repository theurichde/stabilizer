package com.theurich.stabilizer.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileSystemStorageService implements StorageService {

    private static final String NOT_IMPLEMENTED_MESSAGE = "Not implemented yet!";

    @Value("${video.save.root.directory}")
    private Path rootLocation;

    @Override
    public void init() {
        try {
            Files.createDirectories(rootLocation);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void store(final MultipartFile file) {
        final Path path = getFilePath(file.getOriginalFilename());
        try {
            file.transferTo(new File(path.toUri()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Stream<Path> loadAll() {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_MESSAGE);
    }

    @Override
    public Path load(final String filename) {
        return getFilePath(filename);
    }

    private Path getFilePath(final String filename) {
        return Paths.get(rootLocation.toString(), filename);
    }

    @Override
    public Resource loadAsResource(final String filename) {
        final FileSystemResourceLoader fileSystemResourceLoader = new FileSystemResourceLoader();
        return fileSystemResourceLoader.getResource(getFilePath(filename).toString());
    }

    @Override
    public void deleteAll() {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_MESSAGE);
    }
}
