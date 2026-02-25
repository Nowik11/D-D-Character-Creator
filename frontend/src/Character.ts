import type {AbilityScores} from "./AbilityScores.ts";

export interface Character{
    name: string;
    startLvl: number;
    race: string;
    class: string
    isMulticlass: boolean;
    classes: string[];
    abilityScores: AbilityScores;
}
