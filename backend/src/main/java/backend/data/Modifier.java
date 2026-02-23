package backend.data;


import backend.data.enums.ModifierType;

public record Modifier(ModifierType modifierType, String modifierValue) {
}
