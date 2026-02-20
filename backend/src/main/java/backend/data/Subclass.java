package backend.data;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record Subclass(@NotEmpty String name, @NotEmpty String description, List<Feature> features, boolean userCreated) {
}
