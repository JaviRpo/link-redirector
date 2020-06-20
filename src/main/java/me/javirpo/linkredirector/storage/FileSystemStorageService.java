package me.javirpo.linkredirector.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.javirpo.linkredirector.model.Link;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class FileSystemStorageService implements StorageService {
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void store(Link link) throws IOException {
        File linkFile = FileModelUtils.getLinkFile(link.getId());
        linkFile.getParentFile().mkdirs();

        objectMapper.writeValue(linkFile, link);
    }

    @Override
    public Optional<Link> load(String linkId) throws IOException {
        File linkFile = FileModelUtils.getLinkFile(linkId);
        if (linkFile.exists()) {
            Link link = objectMapper.readValue(linkFile, Link.class);
            return Optional.of(link);
        }
        return Optional.empty();
    }

    @Override
    public List<Link> loadAll() throws IOException {
        File linksDir = FileModelUtils.getLinksDir();
        File[] linkFiles = linksDir.listFiles((dir, name) -> name.endsWith(".json"));
        ArrayList<Link> links = new ArrayList<>(linkFiles.length);
        for (File linkFile : linkFiles) {
            Link link = objectMapper.readValue(linkFile, Link.class);
            links.add(link);
        }
        return links;
    }

    @Override
    public boolean delete(String linkId) {
        File linkFile = FileModelUtils.getLinkFile(linkId);
        return linkFile.delete();
    }
}
