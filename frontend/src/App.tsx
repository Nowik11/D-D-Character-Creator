import "./App.css"
import { useEffect, useState } from 'react'
import { CharacterCreator } from './CharacterCreator.tsx'
import { AbilityScoresPage } from "./AbilityScoresPage.tsx";
import type {Character} from "./Character.ts";
interface Feature {
    name: string;
    description: string;
    requiredLevel: number;
    id: string;
}

function App() {
    const [features, setFeatures] = useState<Feature[]>([]);
    const [loading, setLoading] = useState(true);
    const [page, setPage] = useState('list');
    const pageMap = ['list', 'create', 'ability'];
    const navigate = (direction: 'next' | 'prev') => {
        const currentIndex = pageMap.indexOf(page);
        if (direction === 'next' && currentIndex < pageMap.length - 1) {
            setPage(pageMap[currentIndex + 1]);
        } else if (direction === 'prev' && currentIndex > 0) {
            setPage(pageMap[currentIndex - 1]);
        }
    };
    const [characterData, setCharacterData] = useState<Character>({
        name:'',
        startLvl: -1,
        race: '',
        isMulticlass: false,
        classes:[],
        abilityScores: ({
            method: '',
            int: 0,
            str: 0,
            dex: 0,
            con: 0,
            wis: 0,
            cha: 0
        })
        });
    const isCreateStepValid = () => {
        // if (page === 'create') {
        //     // console.log("Obecne dane postaci:", characterData);
        //     return characterData.name.length > 0 &&
        //         characterData.race !== '' &&
        //         characterData.classes.length > 0 &&
        //         characterData.startLvl > 0;
        // }  TEMPORARILY DISABLED - DEVMODE
        return true;
    }
    useEffect(() => {
        fetch('http://localhost:8080/api/features')
            .then(response => {
                if (!response.ok) {
                    throw new Error('Błąd sieci!');
                }
                return response.json();
            })
            .then((data: Feature[]) => {
                const dataWithIDs=data.map(item =>({
                        ...item,
                        id: crypto.randomUUID()
                }));
                setFeatures(dataWithIDs);
                setLoading(false);
            })
            .catch(error => {
                console.error('Problem z pobieraniem:', error);
                setLoading(false);
            });
    }, []);

    if (loading) return <p>Ładowanie danych...</p>;

    function FeatureForm(){
        return(
            <div style={{ padding: '20px' }}>
                <h1>Lista danych z Backendu</h1>
                <ul>
                    {features.map(f => (
                        <li key={f.id}>
                            <p>(ID: {f.id})</p>
                            <p>{f.name}</p>
                            <p>{f.description}</p>
                            <p>{f.requiredLevel}</p>
                        </li>
                    ))}
                </ul>
                {features.length === 0 && <p>Brak danych do wyświetlenia.</p>}
            </div>
        )
    }

    return (
        <>
            <div>
                {(page==='list' && <FeatureForm />)}
                {(page==='create' && <CharacterCreator data={characterData} updateData={setCharacterData}/>)}
                {page==='ability' && <AbilityScoresPage data={characterData} updateData={setCharacterData}/>}
            </div>
            <div style={{padding:'20px'}}/>
            <div style={{
                display: 'flex',
                justifyContent: 'space-between',
                padding: '20px',
                marginTop: '40px',
                borderTop: '1px solid #444'
            }}>
                <button
                    onClick={() => navigate('prev')}
                    disabled={page === pageMap[0]}
                    className="btn-nav"
                >
                    Previous
                </button>

                <button
                    onClick={() => navigate('next')}
                    disabled={page === pageMap[pageMap.length - 1] || !isCreateStepValid()}
                    className="btn-nav"
                >
                    Next
                </button>
            </div>
        </>
    );
}

export default App;