package backend.api.dtos;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record SubclassDto(@NotEmpty String name, @NotEmpty String description, List<FeatureDto> features) {

}
