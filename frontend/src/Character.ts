import type {AbilityScores} from "./AbilityScores.ts";

export interface Character{
    name: string;
    startLvl: number;
    race: string;
    isMulticlass: boolean;
    classes: string[];
    abilityScores: AbilityScores;
}
