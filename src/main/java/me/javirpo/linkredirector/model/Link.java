package me.javirpo.linkredirector.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Link {
    private String id;
    private int timesPerChild;
    @Builder.Default
    private boolean enabled = true;
    @Builder.Default
    private List<LinkChild> children = new ArrayList<>();
}
