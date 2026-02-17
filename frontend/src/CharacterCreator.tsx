import './App.css'
import {useState} from "react";
export function CharacterCreator(){
    const [isMulticlass, setIsMulticlass] = useState<string | null>(null)
    const [isMulticlassOpen, setIsMulticlassOpen] = useState(false)
    const [isClassesOpen, setIsClassesOpen] = useState(false)
    const [isClassOpen, setIsClassOpen] = useState(false)
    const [isRaceOpen, setIsRaceOpen] = useState(false)
    const [selectedClasses, setSelectedClasses] = useState<string[]>([])
    const [selectedClass, setSelectedClass] = useState("")
    const [selectedRace, setSelectedRace]=useState("")

    const handleRaceSelect=(race: string)=>{
        setSelectedRace(race);
        setIsRaceOpen(false)
    }

    const handleMultiClassSelect = (val: string) =>{
        setIsMulticlass(val);
        setIsMulticlassOpen(false);
        if(val==="no") setSelectedClasses([])
    }
    const toggleClass=(className: string) =>{
        setSelectedClasses(prev=>
            prev.includes(className)
            ? prev.filter(c => c!==className) :
            [...prev, className]
        );
    };
    const handleClassSelect=(cls: string)=>{
        setSelectedClass(cls);
        setIsClassOpen(false);
    }
    return(
        <>
            <h1>Welcome to DND character creator!!</h1>
            <p>First, let's learn some basics about thy character:</p>
                <div>
                    <p>What shall your character be named??</p>
                    <input placeholder='Enter name'/>
                </div>
            <div>
                <p>What shall be thy starting level??</p>
                <input placeholder='Enter starting level'/>
            </div>
            <div style={{position: 'relative', width: '200px', marginBottom: '20px'}}>
                <p>Select thy race:</p>
                <div className="select-box" onClick={()=> setIsRaceOpen(!isRaceOpen)}>
                    {
                        selectedRace==="" ?
                        'Select race' :
                        selectedRace
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
            <div style={{position: 'relative', width: '200px', marginBottom: '20px'}}>
                <p>Will thy character be multiclass??</p>
                <div className="select-box" onClick={() => setIsMulticlassOpen(!isMulticlassOpen)}>
                    {
                        isMulticlass===null ?
                        'Select answer' :
                        (isMulticlass==="Yes" ? "Yes" : "No")
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
                {(isMulticlass==="No" &&
                    <div style={{position: 'relative', width: '200px', marginBottom:'20px'}}>
                        <p>Choose thy class</p>
                        <div className="select-box" onClick={()=>setIsClassOpen(!isClassOpen)}>
                            {
                                selectedClass===''?
                                'Select Class' :
                                selectedClass
                            }
                            <span className={`arrow ${isClassOpen ? 'open' : ''}`}>▼</span>
                        </div>
                        {isClassOpen &&
                            <div className="options-container">
                                {["Mage", "Warlock", "Paladin", "Rogue"].map(cls =>(
                                    <div
                                        key={cls}
                                        className="option-label"
                                        onClick={()=>handleClassSelect(cls)}
                                        style ={{cursor: "pointer"}}
                                        >
                                        {cls}
                                    </div>
                                ))}
                            </div>
                        }
                    </div>
                )}
            {(isMulticlass==="Yes" &&
                <div style={{position: 'relative', width: '200px'}}>
                    {
                        selectedClasses.length===1 ?
                            <p style={{color:"red"}}>Only one class selected!</p>:
                            <p>Choose thy classes</p>
                    }
                    <div className="select-box" onClick={() => setIsClassesOpen(!isClassesOpen)}>
                        {
                            selectedClasses.length>0 ?
                            selectedClasses.join(", ") :
                            "Select Classes..."
                        }
                        <span className={`arrow ${isClassesOpen ? 'open' : ''}`}>▼</span>
                    </div>
                    {isClassesOpen &&
                        <div className="options-container">
                            {["Mage", "Warlock", "Paladin", "Rogue"].map(cls => (
                                <label key={cls} className="option-label">
                                    <input
                                        type="checkbox"
                                        checked={selectedClasses.includes(cls)}
                                        onChange={() => toggleClass(cls)}
                                    /> {cls}
                                </label>
                            ))}
                        </div>
                    }
                </div>
            )}
        </>
    )
}
export default CharacterCreator;