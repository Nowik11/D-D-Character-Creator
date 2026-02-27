package backend.data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PositiveOrZero;

public record Item(@NotEmpty String name,
                   String desc,
                   @PositiveOrZero int cost,
                   @PositiveOrZero int weight) {
}
