package backend.data;

import backend.data.enums.AbilityScores;
import backend.data.enums.CastingTime;
import backend.data.enums.SpellComponent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.List;



public record Spell(@NotEmpty String name, @NotEmpty String description,
                    @NotEmpty String schoolOfMagic, @PositiveOrZero int level,
                    @PositiveOrZero int range, @PositiveOrZero int duration,
                    boolean isConcentration, boolean isAttack, boolean userCreated,
                    List<Damage> damage, SpellComponent spellComponent,
                    CastingTime castingTime)   {
}
