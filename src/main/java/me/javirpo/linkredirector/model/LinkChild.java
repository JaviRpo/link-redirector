package me.javirpo.linkredirector.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LinkChild {
    private String to;
    private int count;
    @Builder.Default
    private boolean enabled = true;

    public void increment() {
        count++;
    }
}
