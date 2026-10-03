// api.js
import { API_BASE, players, roundsHistory, roundsCount, tournamentId, setTournamentState } from './state.js';

export async function fetchPlayerFromBackend(id) {
    const response = await fetch(`${API_BASE}/player/${id}`);
    if (!response.ok) throw new Error(`Player not found: ${response.status}`);
    return await response.json();
}

export async function initializeTournamentAPI(count) {
    const cleanedPlayers = players.map(p => ({
        id: (p.id !== null && p.id !== undefined && p.id !== 0) ? p.id : null,
        name: p.name,
        rating: p.rating || 1500,
        score: p.score || 0,
        byeReceived: p.byeReceived || false,
        opponents: {}
    }));

    const payload = {
        id: tournamentId,
        roundsCount: count,
        rounds: roundsHistory,
        players: cleanedPlayers
    };

    const response = await fetch(`${API_BASE}/initial`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
    });

    if (!response.ok) throw new Error(`Server returned status ${response.status}`);
    const data = await response.json();
    setTournamentState(data);
    return data;
}

export async function nextRoundAPI() {
    const cleanedRounds = roundsHistory.map(r => ({
        id: r.id || null,
        round: r.round,
        matches: r.matches.map(m => ({
            id: m.id || null,
            round: r.round,
            player1: m.player1 ? {
                id: m.player1.id || players.find(p => p.name === m.player1.name)?.id || null,
                name: m.player1.name,
                rating: m.player1.rating || 1500,
                score: m.player1.score || 0,
                byeReceived: m.player1.byeReceived || false,
                opponents: {}
            } : null,
            player2: m.player2 ? {
                id: m.player2.id || players.find(p => p.name === m.player2.name)?.id || null,
                name: m.player2.name,
                rating: m.player2.rating || 1500,
                score: m.player2.score || 0,
                byeReceived: m.player2.byeReceived || false,
                opponents: {}
            } : null,
            winPlayer1: m.winPlayer1 === 'draw' ? null : m.winPlayer1,
            colorPlayer1: m.colorPlayer1 !== undefined ? m.colorPlayer1 : true,
            tournmentDto: null
        }))
    }));

    const cleanedPlayers = players.map(p => ({
        id: p.id || null,
        name: p.name,
        rating: p.rating || 1500,
        score: p.score || 0,
        byeReceived: p.byeReceived || false,
        opponents: {}
    }));

    const payload = {
        id: tournamentId,
        roundsCount: roundsCount,
        rounds: cleanedRounds,
        players: cleanedPlayers
    };

    const response = await fetch(`${API_BASE}/next`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
    });

    if (!response.ok) throw new Error(`Server returned status ${response.status}`);
    const data = await response.json();
    setTournamentState(data);
    return data;
}