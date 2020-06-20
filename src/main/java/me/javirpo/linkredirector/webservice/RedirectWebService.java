package me.javirpo.linkredirector.webservice;

import me.javirpo.linkredirector.model.Link;
import me.javirpo.linkredirector.model.LinkChild;
import me.javirpo.linkredirector.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@Validated
@RestController
@RequestMapping("/redirect")
public class RedirectWebService {
    @Autowired
    private StorageService storageService;

    @GetMapping("/{linkId}/next")
    public ResponseEntity<Object> next(@PathVariable String linkId) throws IOException, URISyntaxException {
        Link link = getLink(linkId);

        Optional<LinkChild> linkChildOpt = link.getChildren().stream()
                .filter(LinkChild::isEnabled)
                .filter(linkChild -> linkChild.getCount() < link.getTimesPerChild())
                .findFirst();
        LinkChild linkChild = linkChildOpt.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No more links"));
        linkChild.increment();
        storageService.store(link);

        URI uri = new URI(linkChild.getTo());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(uri);
        return new ResponseEntity<>(httpHeaders, HttpStatus.SEE_OTHER);
    }

    @GetMapping("/{linkId}")
    public ResponseEntity<Link> get(@PathVariable String linkId) throws IOException {
        Link link = getLink(linkId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(link);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Link>> all() throws IOException {
        List<Link> links = storageService.loadAll();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(links);
    }

    @PostMapping("/")
    public ResponseEntity<Link> post(@RequestBody Link link) throws IOException {
        WebServiceValidator.required(link, "link");
        WebServiceValidator.required(link.getId(), "id");
        WebServiceValidator.is(link.getTimesPerChild() > 0, "timesPerChild > 0");

        Optional<Link> linkOpt = getLinkOpt(link.getId());
        if (linkOpt.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Link " + link.getId() + " already exists");
        }
        storageService.store(link);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(link);
    }

    @PutMapping("/")
    public ResponseEntity<Link> put(@RequestBody Link newLink) throws IOException {
        WebServiceValidator.required(newLink, "link");
        WebServiceValidator.required(newLink.getId(), "id");
        WebServiceValidator.is(newLink.getTimesPerChild() > 0, "timesPerChild > 0");

        Link link = getLink(newLink.getId());
        link.setTimesPerChild(newLink.getTimesPerChild());
        if (newLink.isEnabled()) {
            link.setEnabled(newLink.isEnabled());
        }
        storageService.store(link);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(link);
    }

    @DeleteMapping("/{linkId}")
    public ResponseEntity<Void> delete(@PathVariable String linkId) throws IOException {
        Link link = getLink(linkId);
        link.setEnabled(false);
        storageService.store(link);

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .build();
    }

    @PostMapping("/{linkId}/child")
    public ResponseEntity<Link> postChild(@PathVariable String linkId, @RequestBody LinkChild linkChild) throws IOException {
        Link link = getLink(linkId);
        WebServiceValidator.required(linkChild, "linkChild");
        WebServiceValidator.required(linkChild.getTo(), "to");
        linkChild.setCount(0);
        linkChild.setEnabled(true);
        link.getChildren().add(linkChild);

        storageService.store(link);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(link);
    }

    @DeleteMapping("/{linkId}/child")
    public ResponseEntity<Link> deleteChild(@PathVariable String linkId, @RequestBody LinkChild newLinkChild) throws IOException {
        Link link = getLink(linkId);
        WebServiceValidator.required(newLinkChild, "linkChild");
        WebServiceValidator.required(newLinkChild.getTo(), "to");
        Optional<LinkChild> linkChildOpt = link.getChildren().stream()
                .filter(linkChild -> linkChild.isEnabled())
                .filter(linkChild -> linkChild.getTo().equals(newLinkChild.getTo()))
                .findFirst();
        if (linkChildOpt.isPresent()) {
            LinkChild linkChild = linkChildOpt.get();
            linkChild.setEnabled(false);
            storageService.store(link);
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(link);
    }

    private Optional<Link> getLinkOpt(String linkId) throws IOException {
        WebServiceValidator.required(linkId, "linkId");

        return storageService.load(linkId);
    }

    private Link getLink(String linkId) throws IOException {
        Optional<Link> linkOpt = getLinkOpt(linkId);
        return linkOpt.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Link " + linkId + " not found"));
    }
}
