package me.javirpo.linkredirector.storage;

import me.javirpo.linkredirector.model.Link;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface StorageService {
    void store(Link link) throws IOException;

    Optional<Link> load(String linkId) throws IOException;

    List<Link> loadAll() throws IOException;

    boolean delete(String linkId);
}
