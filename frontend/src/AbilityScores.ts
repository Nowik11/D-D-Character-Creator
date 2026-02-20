export interface AbilityScores{
    method: string;
    scores: Record<'str' | 'dex' | 'con' | 'int' | 'wis' | 'cha', number>;
}