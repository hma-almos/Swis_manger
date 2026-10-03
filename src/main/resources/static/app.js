// ==========================================
// app.js - Tournament Frontend Logic
// ==========================================

import { players, roundsHistory, currentRound, roundsCount, viewingRound, setCurrentRound } from './state.js';
import { initializeTournamentAPI, nextRoundAPI, fetchPlayerFromBackend } from './api.js';

// ==========================================
// 1. GLOBAL ACTION HANDLERS (HTML Onclick Bindings)
// ==========================================

window.addPlayer = function() {
    const nameInput = document.getElementById('player-name');
    const ratingInput = document.getElementById('player-rating');
    if (!nameInput || !nameInput.value.trim()) return;

    players.push({
        id: null,
        name: nameInput.value.trim(),
        rating: parseInt(ratingInput.value) || 1500,
        score: 0,
        byeReceived: false,
        opponents: {}
    });

    localStorage.setItem('localPlayers', JSON.stringify(players));
    nameInput.value = '';
    renderPlayerList();
};

window.removePlayer = function(idx) {
    players.splice(idx, 1);
    localStorage.setItem('localPlayers', JSON.stringify(players));
    renderPlayerList();
};

window.fetchPlayerById = async function() {
    const idInput = document.getElementById('fetch-id');
    if (!idInput || !idInput.value) return;
    try {
        const p = await fetchPlayerFromBackend(idInput.value);
        if (p) {
            players.push({
                id: p.id,
                name: p.name,
                rating: p.rating || 1500,
                score: p.score || 0,
                byeReceived: p.byeReceived || false,
                opponents: {}
            });
            localStorage.setItem('localPlayers', JSON.stringify(players));
            renderPlayerList();
            idInput.value = '';
        }
    } catch (e) {
        alert("Could not fetch player from backend.");
    }
};

window.initializeTournament = async function() {
    if (players.length < 2) {
        alert("At least 2 players are required to start a tournament.");
        return;
    }
    const countInput = document.getElementById('rounds-count');
    const count = countInput ? parseInt(countInput.value) : 3;
    if (count < 1) {
        alert("Please select a valid rounds count.");
        return;
    }

    try {
        await initializeTournamentAPI(count);
        localStorage.setItem('currentRound', 1);
        window.location.href = 'dashboard.html';
    } catch (e) {
        console.error(e);
        alert("Failed to initialize tournament on the backend.");
    }
};

window.nextRound = async function() {
    const rObj = roundsHistory.find(r => r.round === currentRound);
    if (rObj && rObj.matches.some(m => m.player2 !== null && (m.winPlayer1 === null || m.winPlayer1 === undefined))) {
        alert("Please set a result for all boards before advancing.");
        return;
    }

    try {
        if (currentRound < roundsCount) {
            await nextRoundAPI();
            // Use the setter function instead of currentRound++
            setCurrentRound(currentRound + 1);
            location.reload();
        } else {
            document.getElementById('next-round-btn').style.display = 'none';
            showWinners();
        }
    } catch (e) {
        console.error(e);
        alert("Failed to sync next round with backend.");
    }
};

window.resetTournament = function() {
    localStorage.clear();
    window.location.href = 'index.html';
};

window.closePlayerModal = function() {
    const modal = document.getElementById('player-modal');
    if (modal) modal.classList.add('hidden');
};

window.switchViewingRound = function(r) {
    viewingRound = r;
    renderDashboard();
};

window.openPlayerProfile = function(name) {
    const p = players.find(x => x.name === name);
    if (!p) return;
    document.getElementById('modal-player-name').innerText = p.name;
    document.getElementById('modal-player-rating').innerText = p.rating;
    document.getElementById('modal-player-score').innerText = p.score;
    document.getElementById('player-modal').classList.remove('hidden');
};

window.updateMatchResult = function(roundNum, matchIdx, resultType) {
    const rObj = roundsHistory.find(r => r.round === roundNum);
    if (!rObj) return;
    const match = rObj.matches[matchIdx];

    if (resultType === 'white') {
        match.winPlayer1 = (match.colorPlayer1 === false) ? false : true;
    } else if (resultType === 'black') {
        match.winPlayer1 = (match.colorPlayer1 === false) ? true : false;
    } else if (resultType === 'draw') {
        match.winPlayer1 = 'draw';
    }

    localStorage.setItem('roundsHistory', JSON.stringify(roundsHistory));
    renderDashboard();
};


// ==========================================
// 2. CORE CALCULATIONS & STANDINGS
// ==========================================

function calculateStandings() {
    // 1. Reset all player scores to 0 first
    players.forEach(p => p.score = 0);

    // 2. Accumulate scores from rounds history up to the current round
    roundsHistory.forEach(r => {
        if (r.round > currentRound) return;
        r.matches.forEach(m => {
            // Handle BYE match explicitly
            if (m.player1 && !m.player2) {
                const foundP1 = players.find(p => p.name === m.player1.name);
                if (foundP1) foundP1.score += 1;
                return;
            }
            if (!m.player1 && m.player2) {
                const foundP2 = players.find(p => p.name === m.player2.name);
                if (foundP2) foundP2.score += 1;
                return;
            }

            // Normal match scoring mapping back to master `players` array
            if (m.winPlayer1 === true) {
                if (m.player1) {
                    const found = players.find(p => p.name === m.player1.name);
                    if (found) found.score += 1;
                }
            } else if (m.winPlayer1 === false) {
                if (m.player2) {
                    const found = players.find(p => p.name === m.player2.name);
                    if (found) found.score += 1;
                }
            } else if (m.winPlayer1 === 'draw') {
                if (m.player1) {
                    const found1 = players.find(p => p.name === m.player1.name);
                    if (found1) found1.score += 0.5;
                }
                if (m.player2) {
                    const found2 = players.find(p => p.name === m.player2.name);
                    if (found2) found2.score += 0.5;
                }
            }
        });
    });

    // 3. Save the updated scores state to localStorage so it persists
    localStorage.setItem('localPlayers', JSON.stringify(players));

    // 4. Return sorted copy for standings display
    return [...players].sort((a, b) => b.score - a.score || b.rating - a.rating);
}


// ==========================================
// 3. UI RENDERING FUNCTIONS
// ==========================================

function renderPlayerList() {
    const tbody = document.getElementById('player-list-body');
    if (!tbody) return;

    tbody.innerHTML = '';
    players.forEach((p, idx) => {
        tbody.innerHTML += `
            <tr class="hover:bg-slate-800/40">
                <td class="py-2 px-3 text-slate-500">${idx + 1}</td>
                <td class="py-2 px-3 font-semibold text-slate-200">${p.name}</td>
                <td class="py-2 px-3">${p.rating}</td>
                <td class="py-2 px-3 text-right">
                    <button onclick="removePlayer(${idx})" class="text-rose-400 hover:text-rose-300">Del</button>
                </td>
            </tr>
        `;
    });

    const rosterCount = document.getElementById('roster-count');
    if (rosterCount) rosterCount.innerText = `${players.length} players`;
}

function renderDashboard() {
    const standingsBody = document.getElementById('standings-body');
    if (!standingsBody) return; // Exit if not on dashboard page

    const sortedStandings = calculateStandings();
    standingsBody.innerHTML = '';

    sortedStandings.forEach((p, idx) => {
        standingsBody.innerHTML += `
            <tr class="hover:bg-slate-800/40 cursor-pointer" onclick="openPlayerProfile('${p.name}')">
                <td class="py-2 px-2 text-slate-500">${idx + 1}</td>
                <td class="py-2 px-2 font-semibold text-slate-200">${p.name}</td>
                <td class="py-2 px-2 text-center text-emerald-400 font-bold">${p.score}</td>
                <td class="py-2 px-2 text-center text-slate-400">0.0</td>
                <td class="py-2 px-2 text-center text-slate-400">0.0</td>
                <td class="py-2 px-2 text-center text-slate-400">0</td>
                <td class="py-2 px-2 text-right">${p.rating}</td>
            </tr>
        `;
    });

    renderMatches();
    renderRoundsTabs();
}

function renderMatches() {
    const container = document.getElementById('matches-container');
    if (!container) return;

    const rObj = roundsHistory.find(r => r.round === viewingRound);
    if (!rObj || !rObj.matches) {
        container.innerHTML = '<div class="text-xs text-slate-500 font-mono">No pairings available for this round.</div>';
        return;
    }

    let html = `
        <div class="overflow-x-auto">
            <table class="w-full text-left font-mono text-xs">
                <thead>
                    <tr class="border-b border-slate-800 text-slate-500">
                        <th class="py-2 px-3">Board</th>
                        <th class="py-2 px-3">White (Left)</th>
                        <th class="py-2 px-3 text-center">Pts</th>
                        <th class="py-2 px-3 text-center">vs</th>
                        <th class="py-2 px-3">Black (Right)</th>
                        <th class="py-2 px-3 text-center">Pts</th>
                        <th class="py-2 px-3 text-right">Result Actions</th>
                    </tr>
                </thead>
                <tbody class="divide-y divide-slate-800/50">
    `;

    rObj.matches.forEach((m, idx) => {
        let whitePlayer, blackPlayer;

        if (m.colorPlayer1 === false) {
            whitePlayer = m.player2;
            blackPlayer = m.player1;
        } else {
            whitePlayer = m.player1;
            blackPlayer = m.player2;
        }

        const wName = whitePlayer ? whitePlayer.name : 'BYE';
        // Always grab the fresh score from the master `players` array using their name
        const foundW = whitePlayer ? players.find(p => p.name === whitePlayer.name) : null;
        const wScore = foundW ? foundW.score : '-';

        const bName = blackPlayer ? blackPlayer.name : 'BYE / None';
        const foundB = blackPlayer ? players.find(p => p.name === blackPlayer.name) : null;
        const bScore = foundB ? foundB.score : '-';

        const isWhiteWin = (m.winPlayer1 === true && whitePlayer === m.player1) || (m.winPlayer1 === false && whitePlayer === m.player2);
        const isBlackWin = (m.winPlayer1 === false && blackPlayer === m.player2) || (m.winPlayer1 === true && blackPlayer === m.player1);
        const isDraw = m.winPlayer1 === 'draw';

        const btnWhiteClass = isWhiteWin ? 'bg-emerald-600 text-white border-emerald-500' : 'bg-slate-900 text-slate-300 border-slate-700 hover:bg-slate-800';
        const btnDrawClass = isDraw ? 'bg-amber-600 text-white border-amber-500' : 'bg-slate-900 text-slate-300 border-slate-700 hover:bg-slate-800';
        const btnBlackClass = isBlackWin ? 'bg-emerald-600 text-white border-emerald-500' : 'bg-slate-900 text-slate-300 border-slate-700 hover:bg-slate-800';

        html += `
            <tr class="hover:bg-slate-900/50">
                <td class="py-2.5 px-3 text-slate-500">#${idx + 1}</td>
                <td class="py-2.5 px-3 font-semibold text-slate-200">${wName}</td>
                <td class="py-2.5 px-3 text-center text-slate-400">${wScore}</td>
                <td class="py-2.5 px-3 text-center text-slate-600">-</td>
                <td class="py-2.5 px-3 font-semibold text-slate-200">${bName}</td>
                <td class="py-2.5 px-3 text-center text-slate-400">${bScore}</td>
                <td class="py-2.5 px-3 text-right space-x-1">
                    <button onclick="updateMatchResult(${viewingRound}, ${idx}, 'white')" class="px-2 py-1 rounded text-xs border transition ${btnWhiteClass}">1-0</button>
                    <button onclick="updateMatchResult(${viewingRound}, ${idx}, 'draw')" class="px-2 py-1 rounded text-xs border transition ${btnDrawClass}">½-½</button>
                    <button onclick="updateMatchResult(${viewingRound}, ${idx}, 'black')" class="px-2 py-1 rounded text-xs border transition ${btnBlackClass}">0-1</button>
                </td>
            </tr>
        `;
    });

    html += `
                </tbody>
            </table>
        </div>
    `;

    container.innerHTML = html;
}

function renderRoundsTabs() {
    const tabsContainer = document.getElementById('round-tabs-container');
    if (!tabsContainer) return;

    tabsContainer.innerHTML = '';
    for (let i = 1; i <= roundsCount; i++) {
        const activeClass = viewingRound === i ? 'bg-emerald-600 text-white' : 'text-slate-400 hover:text-white';
        tabsContainer.innerHTML += `<button onclick="switchViewingRound(${i})" class="px-2.5 py-1 rounded ${activeClass}">R${i}</button>`;
    }
}

function showWinners() {
    const winnerSec = document.getElementById('winners-section');
    const podium = document.getElementById('podium-list');
    if (!winnerSec || !podium) return;

    winnerSec.classList.remove('hidden');
    const sorted = calculateStandings();
    podium.innerHTML = sorted.map((p, i) => `<div>#${i + 1}: ${p.name} - ${p.score} Pts</div>`).join('');
}


// ==========================================
// 4. INITIALIZATION HOOKS
// ==========================================

document.addEventListener('DOMContentLoaded', () => {
    renderPlayerList();
    renderDashboard();
});