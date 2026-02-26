import './App.css';
import {type SetStateAction, useEffect, useState} from "react";
import type {Character} from "./Character.ts";
import * as React from "react";

interface Props{
    data: Character;
    updateData: React.Dispatch<SetStateAction<Character>>
}
interface statProps extends Props{
    name: string;
    ability: Record<"str" | "dex" | "con" | "int" | "wis" | "cha", number>;
    modifierValue: number;
    points?: number;
}

interface displayProps extends Props{
    points?:number;
}

const statsOrder = ["str", "dex", "con", "int", "wis", "cha"] as const


const calculatePointsLeft = (scores: Record<string, number>, points: number) => {
    const costMap: Record<number, number> = {
        8: 0, 9: 1, 10: 2, 11: 3, 12: 4, 13: 5, 14: 7, 15: 9
    };

    const totalSpent = Object.values(scores).reduce((sum, val) => {
        const cost = costMap[val] || 0;
        return sum + cost;
    }, 0);

    return points - totalSpent;
};

const handleRoll = ({data, updateData}:Props) => {
    const randArr = Array.from({length: 6}, () => Math.floor(Math.random() * 21));
    const newScoresList: Record<"str" | "dex" | "con" | "int" | "wis" | "cha", number> = ({
        str: randArr[0],
        dex: randArr[1],
        con: randArr[2],
        int: randArr[3],
        wis: randArr[4],
        cha: randArr[5],
    });

    updateData({
        ...data,
        abilityScores: {
            ...data.abilityScores,
            scores: newScoresList
        }
    });
}

export function MethodSelector({data, updateData}: Props){
    const [isAbilityOpen, setIsAbilityOpen]=useState(false);
    const dropdownRef = React.useRef<HTMLDivElement>(null);
    useEffect(() => {
        const handleClickOutside = (event: MouseEvent) => {
            if (dropdownRef.current && !dropdownRef.current.contains(event.target as Node)) {
                setIsAbilityOpen(false);
            }
        }
        document.addEventListener('click', handleClickOutside);
        return () => {
            document.removeEventListener('click', handleClickOutside);
        };
    }, []);
    const handleAbilitySelect = (val: string)=>{
        const startVal = val === "Point Buy" ? 7 : -1;
        const newScores = {
            ...data.abilityScores,
            method: val,
            scores: {
                int: startVal,
                cha: startVal,
                str: startVal,
                dex: startVal,
                con: startVal,
                wis: startVal,
            }
        };
        updateData({ ...data, abilityScores: newScores });
        console.log("Wybrana metoda:", val);
        setIsAbilityOpen(false);
    }
    return(
        <div style={{ maxWidth: '1000px', margin: '0 auto' }}>
            <div className="input-group" style={{position: 'relative'}} ref={dropdownRef}>
                <p>Select the way, thy ability scores shall be calculated</p>
                <div className="select-box" tabIndex={0} onClick={() => (setIsAbilityOpen(!isAbilityOpen))}>
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

export function StdArrayStatModule({name, ability, modifierValue, data, updateData}:statProps){
    const currentVal = ability[name as keyof typeof ability];
    const standardArray = [15, 14, 13, 12, 10, 8];
    const usedValues = Object.values(ability).filter(v=> v!==-1);
    const availableValues = standardArray.filter(v => !usedValues.includes(v) || v === currentVal);
    return(
        <div style={{ width: "60px", display: "flex", flexDirection: "column", alignItems: "center" }}>
            <p style={{
                padding: '5px',
                textAlign: "center",
                width: "100%",
                margin: "0 0 5px 0",
                fontSize: "15px",
                fontWeight: "bold"}}>{name.toUpperCase()}</p>
            <div className={'ability-container'}>
                <select
                    className={'ability-input'}
                    style={{color: "#cccccc", appearance: "none", cursor: "pointer"}}
                    value={(ability[name as keyof typeof ability]===-1 ? '':ability[name as keyof typeof ability])}
                    onChange={(e)=>{
                        const val=parseInt(e.target.value, 10);
                        const finalValue=(isNaN(val)? -1: val);
                        const newScoresList = { ...ability, [name]: finalValue };
                        updateData({ ...data, abilityScores: { ...data.abilityScores, scores: newScoresList }})
                    }}
                >
                    <option value="" hidden={true} disabled={false}/>
                    {availableValues.map((val)=>(
                        <option key={val} value={val}>
                            {val}
                        </option>
                    ))}
                    <option value="-1" style={{ color: '#ff4444', textAlign: 'center', justifyContent: 'center' }}>
                        {"X"}
                    </option>
                </select>
                <div className={"ability-modifier"}>
                    {modifierValue}
                </div>
            </div>
        </div>
    );
}

export function StatModule({name, ability, modifierValue, data, updateData, points}: statProps){
    const method=data.abilityScores.method;
    const updateScores = (statName: string, newValue: number) => {
        const newScoresList = {
            ...data.abilityScores.scores,
            [statName]: newValue
        };
        updateData({
            ...data,
            abilityScores: {
                ...data.abilityScores,
                scores: newScoresList
            }
        });
    };
    return(
        <div style={{ width: "60px", display: "flex", flexDirection: "column", alignItems: "center" }}>
            <p style={{
                padding: '5px',
                textAlign: "left",
                width: "100%",
                margin: "0 0 5px 0",
                fontSize: "15px",
                fontWeight: "bold"}}>{name.toUpperCase()}</p>
                <div className={'ability-container'}>
                        <input
                            type={"number"}
                            className={'ability-input'}
                            readOnly={method === 'Random'}
                            value={method==="Point Buy" ?
                                (ability[name as keyof typeof ability] <8 ? "" : ability[name as keyof typeof ability]) :
                                (ability[name as keyof typeof ability] === -1 ? "" : ability[name as keyof typeof ability])}
                            onChange={(e)=>{
                                const rawValue = e.target.value;
                                const parsedValue = parseInt(rawValue, 10);
                                const isPointBuy= method==="Point Buy"
                                const minVal = isPointBuy ? 8 : 0;
                                const maxVal = isPointBuy ? 15 : 20;
                                if (isNaN(parsedValue)) {
                                    const resetValue = isPointBuy ? 7 : -1;
                                    updateScores(name, resetValue)
                                    return;
                                }
                                const targetValue= Math.min(Math.max(parsedValue, minVal), maxVal);
                                const currentValue = ability[name as keyof typeof ability];
                                if(isPointBuy) {
                                    const costMap: Record<number, number> = {
                                        8: 0, 9: 1, 10: 2, 11: 3, 12: 4, 13: 5, 14: 7, 15: 9
                                    }
                                    const currentCost = costMap[currentValue] || 0;
                                    const targetCost = costMap[targetValue] || 0;
                                    const costDifference = targetCost - currentCost;
                                    if(points!==undefined && points>=costDifference){
                                        updateScores(name, targetValue);
                                        points-=costDifference;
                                    }else if (costDifference<0){
                                        updateScores(name, targetValue)
                                    }

                                }else{
                                    updateScores(name, targetValue);
                                }


                                }}
                            onKeyDown={(e) => {
                                const invalidChars = ['-', '+', 'e', 'E', '.', ','];
                                if (invalidChars.includes(e.key)) {
                                    e.preventDefault();
                                }
                            }}
                        />
                    <div className={"ability-modifier"}>
                        {modifierValue}
                    </div>
                </div>
        </div>
    );
}

export function DisplayArray({data, updateData, points}:displayProps){
    return(
        <>{points!==undefined && <p>Points Left: {points}</p>}
            <div style={{display:"flex", justifyContent: "center",alignItems: "center", gap:"30px"}}>
                {statsOrder.map((stat) => (
                data.abilityScores.method !=="Standard Array"? (
                        <StatModule
                        name={stat}
                        ability={data.abilityScores.scores}
                        modifierValue={0}
                        data={data}
                        updateData={updateData}
                        points={data.abilityScores.method==="Point Buy"? points: NaN}
                        />
                    ) :
                    (<StdArrayStatModule
                        name={stat}
                        ability={data.abilityScores.scores}
                        modifierValue={0}
                        data={data}
                        updateData={updateData}/>)
            ))}
        </div>
    </>
    );
}

export function AbilityScoresPage({data, updateData}: Props){
    const pointsLeft = calculatePointsLeft(data.abilityScores.scores, 27);
    return(
        <div style={{ maxWidth: '1000px', margin: '0 auto' }}>
            <MethodSelector data={data} updateData={updateData}/>
            <div style={{padding: '10px'}}>
                {data.abilityScores.method==="Random" ?
                    <div style={{display: "flex", flexDirection: "column", alignItems: "center", gap:"10px" }}>
                        <DisplayArray data={data} updateData={updateData}/>
                        <button onClick={()=>handleRoll({data, updateData})}>roll</button>
                    </div> :
                        data.abilityScores.method==="Point Buy" ?
                            <DisplayArray data={data} updateData={updateData} points={pointsLeft}/> :
                        <DisplayArray data={data} updateData={updateData}/>
                }
            </div>
        </div>
    );
}

export default AbilityScoresPage;