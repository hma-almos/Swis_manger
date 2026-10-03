// state.js
export let players = JSON.parse(localStorage.getItem('localPlayers')) || [];
export let roundsCount = parseInt(localStorage.getItem('roundsCount')) || 0;
export let currentRound = parseInt(localStorage.getItem('currentRound')) || 1;
export let roundsHistory = JSON.parse(localStorage.getItem('roundsHistory')) || [];
export let viewingRound = currentRound; // Now currentRound is already defined above!
export let tournamentCompleted = false;
export let tournamentId = JSON.parse(localStorage.getItem('tournamentId')) || null;

export const API_BASE = '/home';

export function setCurrentRound(val) {
    currentRound = val;
    localStorage.setItem('currentRound', currentRound);
}

export function setTournamentState(newState) {
    if (newState.id !== undefined) {
        tournamentId = newState.id;
        localStorage.setItem('tournamentId', JSON.stringify(tournamentId));
    }
    if (newState.roundsCount !== undefined) {
        roundsCount = newState.roundsCount;
        localStorage.setItem('roundsCount', roundsCount);
    }
    if (newState.rounds) {
        roundsHistory = newState.rounds;
        localStorage.setItem('roundsHistory', JSON.stringify(roundsHistory));
    }
    if (newState.players) {
        newState.players.forEach(savedP => {
            const localP = players.find(p => p.name === savedP.name);
            if (localP) localP.id = savedP.id;
        });
        localStorage.setItem('localPlayers', JSON.stringify(players));
    }
}