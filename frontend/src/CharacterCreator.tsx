import './App.css'
import {type SetStateAction, useState} from "react";
import type {Character} from "./Character.ts";
interface Props{
    data: Character;
    updateData: React.Dispatch<SetStateAction<Character>>
}

export function CharacterCreator({ data, updateData }: Props){


    const [isMulticlassOpen, setIsMulticlassOpen] = useState(false)
    const [isClassesOpen, setIsClassesOpen] = useState(false)
    const [isRaceOpen, setIsRaceOpen] = useState(false)



    const handleRaceSelect=(race: string)=>{
        updateData({...data, race: race})
        setIsRaceOpen(false);
    }

    const handleMultiClassSelect = (val: string) =>{
        const isMulti = val === "Yes";
        updateData({
            ...data,
            isMulticlass: isMulti,
            classes: isMulti ? data.classes : []
        })
        setIsMulticlassOpen(false);
    }

    const toggleClass=(className: string) =>{
        const isSelected = data.classes.includes(className);
        const updatedClasses = isSelected
            ? data.classes.filter(c => c !== className)
            : [...data.classes, className];
        updateData({ ...data, classes: updatedClasses });
    };

    const closeSelectBoxes=() =>{
        setIsMulticlassOpen(false);
        setIsRaceOpen(false);
        setIsClassesOpen(false);
    }
    return(
        <div style={{ maxWidth: '1000px', margin: '0 auto' }}>
            <h1>Welcome to DND character creator!!</h1>
            <div className="form-container">
                <p>First, let's learn some basics about thy character:</p>
                    <div className="input-group">
                        <p>What shall your character be named??</p>
                        <input
                            value={data.name}
                            placeholder={'Enter name'}
                               onChange={(e) =>
                                   updateData({...data, name: e.target.value})}/>
                    </div>
                    <div className="input-group">
                        <p>What shall be thy starting level??</p>
                        <input
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
                <div className="input-group" style={{position: 'relative'}}>
                    <p>Select thy race:</p>
                    <div className="select-box" onClick={()=> (closeSelectBoxes(), setIsRaceOpen(!isRaceOpen))}>
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
                <div className="input-group" style={{position: 'relative'}}>
                    <p>Will thy character be multiclass??</p>
                    <div className="select-box" onClick={() => (closeSelectBoxes(), setIsMulticlassOpen(!isMulticlassOpen))}>
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
                <div className="input-group" style={{position: 'relative'}}>
                    {
                        data.isMulticlass ?
                            (data.classes.length===1 ?
                            <p style={{color:"red"}}>Only one class selected!</p>:
                            <p>Choose thy classes</p>) :
                            (<p>Choose thy class</p>)
                    }
                    <div className="select-box" onClick={() =>(closeSelectBoxes(), setIsClassesOpen(!isClassesOpen))}>
                        {
                            data.isMulticlass ?
                                (data.classes.length>0 ?
                                data.classes.join(", ") :
                                "Select Classes...") :
                                (data.classes.length===0?
                                    'Select Class...' :
                                    data.classes[0])
                        }
                        <span className={`arrow ${isClassesOpen ? 'open' : ''}`}>▼</span>
                    </div>
                    {isClassesOpen &&
                        (data.isMulticlass ?
                            (<div className="options-container">
                            {["Mage", "Warlock", "Paladin", "Rogue"].map(cls => (
                                <label key={cls} className="option-label">
                                    <input
                                        type="checkbox"
                                        checked={data.classes.includes(cls)}
                                        onChange={() => toggleClass(cls)}
                                    /> {cls}
                                </label>
                            ))}
                        </div>):
                            (<div className="options-container">
                                {["Mage", "Warlock", "Paladin", "Rogue"].map(cls =>(
                                    <div
                                        key={cls}
                                        className="option-label"
                                        onClick={()=>{
                                            updateData({...data, classes: [cls]});
                                            setIsClassesOpen(false);
                                        }}
                                        style ={{cursor: "pointer"}}
                                    >
                                        {cls}
                                    </div>
                                ))}
                            </div>))
                    }
                </div>
            </div>
        </div>
    )
}
export default CharacterCreator;