import './App.css';
import {type SetStateAction, useState} from "react";
import type {Character} from "./Character.ts";
import * as React from "react";
interface Props{
    data: Character;
    updateData: React.Dispatch<SetStateAction<Character>>
}
export function AbilityScoresPage({data, updateData}: Props){

    const [isAbilityOpen, setIsAbilityOpen]=useState(false);
    const handleAbilitySelect = (val: string)=>{

        const newScores={...data.abilityScores, method:val}
        updateData({ ...data, abilityScores: newScores });
        console.log("Wybrana metoda:", val);
        setIsAbilityOpen(false);
    }

    return(
        <div style={{ maxWidth: '1000px', margin: '0 auto' }}>
            <div className="input-group" style={{position: 'relative'}}>
                <p>Select the way, thy ability scores shall be calculated</p>
                <div className="select-box" onClick={() => (setIsAbilityOpen(!isAbilityOpen))}>
                    {
                        data.abilityScores.method==="" ?
                            'Select answer' :
                            data.abilityScores.method
                    }
                    <span className={`arrow ${isAbilityOpen ? 'open' : ''}`}>▼</span>
                </div>
                {isAbilityOpen &&
                    <div className="options-container">
                        {["Standard Array", "Point Buy", "Random", "Set"].map(val=>(
                            <div
                                key={val}
                                className="option-label"
                                onClick={() => handleAbilitySelect(val)}
                                style={{ cursor: 'pointer' }}>
                                {val}
                            </div>
                        ))}
                    </div>
                }
            </div>
        </div>
    );
}
export default AbilityScoresPage;