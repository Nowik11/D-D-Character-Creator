import "./App.css"
import { useEffect, useState } from 'react'
import { CharacterCreator } from './CharacterCreator.tsx'

interface Feature {
    name: string;
    description: string;
    requiredLevel: number;
    id: string;
}

function App() {
    const [features, setFeatures] = useState<Feature[]>([]);
    const [loading, setLoading] = useState(true);
    const [page, setPage] = useState('list')

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
                {(page==='create' && <CharacterCreator />)}
            </div>
        <button onClick={() => page==='list' ? setPage('create') : setPage('list')}>
            {page==='list' ? 'create character' : 'return to list'}</button>
        </>
    );
}

export default App;