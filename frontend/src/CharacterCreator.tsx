import './App.css'
import {type SetStateAction, useEffect, useRef, useState} from "react";
import type {Character} from "./Character.ts";
interface Props{
    data: Character;
    updateData: React.Dispatch<SetStateAction<Character>>
}

export function NameInput({ data, updateData }: Props){
    return(
        <div className="input-group" >
            <p>What shall your character be named??</p>
            <input
                className={'input-bar'}
                value={data.name}
                placeholder={'Enter name'}
                onChange={(e) =>
                    updateData({...data, name: e.target.value})}/>
        </div>
    );
}

export function StartingLvlInput({ data, updateData }: Props){
    return(
        <div className="input-group">
            <p>What shall be thy starting level??</p>
            <input
                className={'input-bar'}
                value={data.startLvl === -1 ? '' : data.startLvl}
                type="number"
                placeholder={'Enter starting level'}
                onChange={(e) => {
                    const target = e.target as HTMLInputElement;
                    const val = target.valueAsNumber;
                    updateData({
                        ...data,
                        startLvl: isNaN(val) ? -1 : val
                    });
                }}/>
        </div>
    );
}

export function RaceSelector({ data, updateData }:Props){
    const [isRaceOpen, setIsRaceOpen] = useState(false)
    const dropdownRef = useRef<HTMLDivElement>(null);
    useEffect(() => {
        const handleClickOutside = (event: MouseEvent) => {
            if (dropdownRef.current && !dropdownRef.current.contains(event.target as Node)) {
                setIsRaceOpen(false);
            }
        };
        document.addEventListener("mousedown", handleClickOutside);
        return () => document.removeEventListener("mousedown", handleClickOutside);
    }, []);
    const handleRaceSelect=(race: string)=>{
        updateData({...data, race: race})
        setIsRaceOpen(false);
    }
    return(
        <div className="input-group" style={{position: 'relative'}} ref={dropdownRef}>
            <p>Select thy race:</p>
            <div className="select-box" tabIndex={0} onClick={()=> {
                setIsRaceOpen(!isRaceOpen);
            }}>
                {
                    data.race==="" ?
                        'Select race' :
                        data.race
                }
                <span className={`arrow ${isRaceOpen ? 'open' : ''}`}>▼</span>
            </div>
            {isRaceOpen &&
                <div className="options-container">
                    {["Human", "Dwarf", "Elf", "Dragonborn"].map(race =>(
                        <div
                            key={race}
                            className="option-label"
                            onClick={()=>handleRaceSelect(race)}
                            style={{cursor:"pointer"}}>
                            {race}
                        </div>
                    ))}
                </div>
            }
        </div>
    );
}

export function MainClassSelector({data, updateData}: Props){
    const [isClassOpen, setIsClassOpen]=useState(false)
    const dropdownRef = useRef<HTMLDivElement>(null);
    useEffect(() => {
        const handleClickOutside = (event: MouseEvent) => {
            if (dropdownRef.current && !dropdownRef.current.contains(event.target as Node)) {
                setIsClassOpen(false);
            }
        };
        document.addEventListener("mousedown", handleClickOutside);
        return () => document.removeEventListener("mousedown", handleClickOutside);
    }, []);
    return(
        <div className="input-group" style={{position: 'relative'}} ref={dropdownRef}>
            <p>Choose thy class</p>
            <div className="select-box" tabIndex={0} onClick={() =>{
                setIsClassOpen(!isClassOpen)}}>
                {
                    data.class===''? "Select your main class" : data.class
                }
                <span className={`arrow ${isClassOpen ? 'open' : ''}`}>▼</span>
            </div>
            {isClassOpen &&
                <div className="options-container">
                    {["Mage", "Warlock", "Paladin", "Rogue"].map(cls =>(
                        <div
                            key={cls}
                            className="option-label"
                            onClick={()=>{
                                updateData({...data, class: cls});
                                setIsClassOpen(false);
                            }}
                            style ={{cursor: "pointer"}}
                        >
                            {cls}
                        </div>
                    ))}
                </div>

            }
        </div>
    );
}

export function MulticlassQuestionSelector({data, updateData}:Props){
    const [isMulticlassOpen, setIsMulticlassOpen] = useState(false)
    const dropdownRef = useRef<HTMLDivElement>(null);
    useEffect(() => {
        const handleClickOutside = (event: MouseEvent) => {
            if (dropdownRef.current && !dropdownRef.current.contains(event.target as Node)) {
                setIsMulticlassOpen(false);
            }
        };
        document.addEventListener("mousedown", handleClickOutside);
        return () => document.removeEventListener("mousedown", handleClickOutside);
    }, []);
    const handleMultiClassSelect = (val: string) =>{
        const isMulti = val === "Yes";
        updateData({
            ...data,
            isMulticlass: isMulti,
            classes: isMulti ? data.classes : []
        })
        setIsMulticlassOpen(false);
    }
    return(
        <div className="input-group" style={{position: 'relative'}} ref={dropdownRef}>
            <p>Will thy character be multiclass??</p>
            <div className="select-box" tabIndex={0} onClick={() => {
                setIsMulticlassOpen(!isMulticlassOpen)
            }}>
                {
                    data.isMulticlass? "Yes" : "No"
                }
                <span className={`arrow ${isMulticlassOpen ? 'open' : ''}`}>▼</span>
            </div>
            {isMulticlassOpen &&
                <div className="options-container">
                    {["Yes", "No"].map(val=>(
                        <div
                            key={val}
                            className="option-label"
                            onClick={() => handleMultiClassSelect(val)}
                            style={{ cursor: 'pointer' }}>
                            {val}
                        </div>
                    ))}
                </div>
            }
        </div>
    );
}

export function MulticlassSelector({data, updateData}: Props){
    const [isClassesOpen, setIsClassesOpen] = useState(false)
    const dropdownRef = useRef<HTMLDivElement>(null);
    useEffect(() => {
        const handleClickOutside = (event: MouseEvent) => {
            if (dropdownRef.current && !dropdownRef.current.contains(event.target as Node)) {
                setIsClassesOpen(false);
            }
        };
        document.addEventListener("mousedown", handleClickOutside);
        return () => document.removeEventListener("mousedown", handleClickOutside);
    }, []);
    const toggleClass=(className: string) =>{
        const isSelected = data.classes.includes(className);
        const updatedClasses = isSelected
            ? data.classes.filter(c => c !== className)
            : [...data.classes, className];
        updateData({ ...data, classes: updatedClasses });
    };
    return(
        <div className="input-group" style={{position: 'relative'}} ref={dropdownRef}>
            {
                (data.classes.length===1 ?
                    <p style={{color:"red"}}>Only one class selected!</p>:
                    <p>Choose thy classes</p>)
            }
            <div className="select-box" tabIndex={0} onClick={() =>{
                setIsClassesOpen(!isClassesOpen)}}>
                {
                    (data.classes.length>0 ?
                        data.classes.join(", ") :
                        "Select Classes...")
                }
                <span className={`arrow ${isClassesOpen ? 'open' : ''}`}>▼</span>
            </div>
            {isClassesOpen &&
                <div className="options-container">
                    {["Mage", "Warlock", "Paladin", "Rogue"].filter(cls=>
                        cls!==data.class).map(cls => (
                        <label key={cls} className="option-label">
                            <input
                                type="checkbox"
                                checked={data.classes.includes(cls)}
                                onChange={() => toggleClass(cls)}
                            /> {cls}
                        </label>
                    ))}
                </div>
            }
        </div>
    );
}

export function CharacterCreator({ data, updateData }: Props){
    return(
        <div style={{ maxWidth: '1000px', margin: '0 auto' }}>
            <h1>Welcome to DND character creator!!</h1>
            <p>First, let's learn some basics about thy character:</p>
            <div className={"form-container"}>
                <NameInput data={data} updateData={updateData}/>
                <StartingLvlInput data={data} updateData={updateData}/>
                <RaceSelector data={data} updateData={updateData}/>
                <MainClassSelector data={data} updateData={updateData}/>
                <MulticlassQuestionSelector data={data} updateData={updateData}/>
                {data.isMulticlass &&
                    <MulticlassSelector data={data} updateData={updateData}/>
                }
            </div>
        </div>
    )
}
export default CharacterCreator;