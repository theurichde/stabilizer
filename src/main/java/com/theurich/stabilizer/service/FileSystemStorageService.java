package com.theurich.stabilizer.service;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;
import java.util.stream.Stream;

import com.theurich.stabilizer.util.PathUtil;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileSystemStorageService implements StorageService {

    private static final String NOT_IMPLEMENTED_MESSAGE = "Not implemented yet!";

    @Override
    public void init() {
        try {
            final Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rwxrwxrwx");
            final FileAttribute<Set<PosixFilePermission>> attr = PosixFilePermissions.asFileAttribute(perms);
            Files.createDirectory(PathUtil.ROOT_LOCATION, attr);
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public URI store(final MultipartFile file) {
        final Path path = getFilePath(file.getOriginalFilename());
        try {
            Files.write(path, file.getBytes());
            return path.toUri();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
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
        return Paths.get(PathUtil.ROOT_LOCATION.toString(), filename);
    }

    @Override
    public Resource loadAsResource(final String filename) {
        final FileSystemResourceLoader fileSystemResourceLoader = new FileSystemResourceLoader();
        return fileSystemResourceLoader.getResource(filename);
    }

    @Override
    public void deleteAll() {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED_MESSAGE);
    }
}
