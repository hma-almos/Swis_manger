package com.example.demo.Services;

import com.example.demo.dto.*;
import com.example.demo.entity.MatchEntity;
import com.example.demo.entity.PlayerEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CenteralService {

    @Autowired
    private TournmentService tournmentService;
    @Autowired
    private RoundService roundService;
    @Autowired
    private MatchService matchService;
    @Autowired
    private PlayerService playerService;

    public TournmentDto initialMatching(TournmentDto tournment) {
        // 1. Save the tournament FIRST so it gets an ID assigned in the database
        List<PlayerDto> players = new ArrayList<>(playerService.insertAll(tournment.players()));

        TournmentDto savedTournment = tournmentService.insert(tournment.clone(players,null));

        // 2. Insert players
        List<MatchDto> matchDtos = new ArrayList<>();

        // 3. Pass the *saved* tournament (with the generated ID) to the matching logic
        partOfRound(players, 0, matchDtos, savedTournment);

        if (players.size() % 2 != 0) {
            MatchDto match = new MatchDto(null, 1, players.get(players.size() - 1).updateBY(), null, null, true, savedTournment);
            matchDtos.add(match);
        }

        // 4. Now that matchDtos have the proper tournament reference, insert them safely
        RoundDto round = roundService.insert(new RoundDto(null, matchService.insertAll(matchDtos), 1));
//        savedTournment.addRound(roundService.insert(round));
        List<RoundDto> rounds =new ArrayList<>();
        rounds.add(round);
        // Optional: update tournament again if rounds list needs syncing


        return tournmentService.update(savedTournment.clone(players,rounds));
    }

    public TournmentDto nextRound(TournmentDto tournment) {
        List<RoundDto> rounds = tournment.rounds();
        if (rounds == null || rounds.isEmpty()) {
            return initialMatching(tournment);
        }

        int latestRoundNum = rounds.get(rounds.size() - 1).round();

        if (latestRoundNum >= tournment.roundsCount()) {
            matchService.updateAll(rounds.get(rounds.size() - 1).matches());
            roundService.update(rounds.get(rounds.size() - 1));
            tournmentService.update(tournment);
            updateRating(tournment);
            return tournment;
        }

        List<PlayerDto> players = tournment.players() != null ? new ArrayList<>(tournment.players()) : new ArrayList<>();
        List<MatchDto> matchDtos = new ArrayList<>();
        PlayerDto lastPlayer = null;
        int currentScore = latestRoundNum;

        while (currentScore >= 0) {
            final int score = currentScore;
            List<PlayerDto> scoreGroup = new ArrayList<>(players.stream().filter(p -> p.score() == score).toList());

            if (lastPlayer != null) {
                if (!scoreGroup.isEmpty()) {
                    boolean color = Math.random() >= 0.5;
                    MatchDto match = new MatchDto(null, latestRoundNum + 1, lastPlayer, scoreGroup.get(0), null, color, tournment);
                    matchDtos.add(match);
                    scoreGroup.remove(0);
                }
                lastPlayer = null;
            }

            partOfRound(scoreGroup, latestRoundNum, matchDtos, tournment);

            if (scoreGroup.size() % 2 != 0) {
                if (currentScore != 0) {
                    lastPlayer = scoreGroup.get(scoreGroup.size() - 1);
                    scoreGroup.remove(scoreGroup.size() - 1);
                } else {
                    MatchDto match = new MatchDto(null, latestRoundNum + 1, scoreGroup.get(scoreGroup.size() - 1).updateBY(), null, null, true, tournment);
                    matchDtos.add(match);
                }
            }
            currentScore--;
        }

        if (lastPlayer != null) {
            MatchDto match = new MatchDto(null, latestRoundNum + 1, lastPlayer.updateBY(), null, null, true, tournment);
            matchDtos.add(match);
        }

        RoundDto round = new RoundDto(null, matchService.insertAll(matchDtos), latestRoundNum + 1);
        tournment.addRound(roundService.insert(round));
        tournmentService.update(tournment);
        return tournment;
    }

    private void partOfRound(List<PlayerDto> players, int currentRound, List<MatchDto> roundList, TournmentDto tournmentDto) {
        if (players == null || players.size() < 2) {
            return;
        }

        List<PlayerDto> pool = new ArrayList<>(players);
        Collections.shuffle(pool);

        List<Pair> pairings = findValidPairings(pool, new ArrayList<>(), new HashSet<>());
        Set<Integer> pairedIds = new HashSet<>();

        if (pairings != null && !pairings.isEmpty()) {
            for (Pair p : pairings) {
                boolean color = checkColor(p.player1, p.player2);
                MatchDto match = new MatchDto(null, currentRound + 1, p.player1, p.player2, null, color, tournmentDto);
                roundList.add(match);
                pairedIds.add(p.player1.id());
                pairedIds.add(p.player2.id());
            }
        } else {
            List<PlayerDto> strong = new ArrayList<>(pool.subList(0, pool.size() / 2));
            List<PlayerDto> weak = new ArrayList<>(pool.subList(pool.size() / 2, pool.size()));

            for (int i = 0; i < Math.min(strong.size(), weak.size()); i++) {
                PlayerDto strongPlayer = strong.get(i);
                PlayerDto weakPlayer = weak.get(i);

                int attempts = 0;
                while (strongPlayer.opponents() != null &&
                        strongPlayer.opponents().containsKey(weakPlayer.name()) &&
                        attempts < weak.size() * 2) {
                    Collections.shuffle(weak);
                    weakPlayer = weak.get(i % weak.size());
                    attempts++;
                }

                boolean color = checkColor(strongPlayer, weakPlayer);
                MatchDto match = new MatchDto(null, currentRound + 1, strongPlayer, weakPlayer, null, color, tournmentDto);
                roundList.add(match);
                pairedIds.add(strongPlayer.id());
                pairedIds.add(weakPlayer.id());
            }
        }

        // Safely remove only successfully paired players, keeping any odd/unpaired players in the list
        try {
            players.removeIf(p -> pairedIds.contains(p.id()));
        } catch (UnsupportedOperationException e) {
            // Handled if list is immutable, though wrapped in ArrayList it will succeed
        }
    }

    private static class Pair {
        PlayerDto player1;
        PlayerDto player2;
        public Pair(PlayerDto p1, PlayerDto p2) {
            this.player1 = p1;
            this.player2 = p2;
        }
    }

    private List<Pair> findValidPairings(List<PlayerDto> remaining, List<Pair> currentPairings, Set<Integer> usedIds) {
        if (remaining.isEmpty()) {
            return currentPairings;
        }

        PlayerDto first = null;
        int firstIdx = -1;
        for (int i = 0; i < remaining.size(); i++) {
            if (!usedIds.contains(remaining.get(i).id())) {
                first = remaining.get(i);
                firstIdx = i;
                break;
            }
        }

        if (first == null) return currentPairings;

        usedIds.add(first.id());

        for (int j = 0; j < remaining.size(); j++) {
            PlayerDto second = remaining.get(j);
            if (j != firstIdx && !usedIds.contains(second.id())) {
                boolean alreadyPlayed = (first.opponents() != null && first.opponents().containsKey(second.name())) ||
                        (second.opponents() != null && second.opponents().containsKey(first.name()));

                if (!alreadyPlayed) {
                    usedIds.add(second.id());
                    currentPairings.add(new Pair(first, second));

                    List<Pair> result = findValidPairings(remaining, currentPairings, usedIds);
                    if (result != null) {
                        return result;
                    }

                    currentPairings.remove(currentPairings.size() - 1);
                    usedIds.remove(second.id());
                }
            }
        }

        usedIds.remove(first.id());
        return null;
    }

    private boolean checkColor(PlayerDto playerDto, PlayerDto weakDto) {
        if (playerDto.opponents() == null || playerDto.opponents().isEmpty() ||
                weakDto.opponents() == null || weakDto.opponents().isEmpty()) {
            return Math.random() >= 0.5;
        }

        String[] keySet = playerDto.opponents().keySet().toArray(new String[0]);
        String[] weakKeySet = weakDto.opponents().keySet().toArray(new String[0]);

        if (keySet.length < 2 || weakKeySet.length < 2) {
            return Math.random() >= 0.5;
        }

        boolean lastColor = playerDto.opponents().get(keySet[keySet.length - 1]).color();
        boolean beforeColor = playerDto.opponents().get(keySet[keySet.length - 2]).color();

        boolean weakLastColor = weakDto.opponents().get(weakKeySet[weakKeySet.length - 1]).color();
        boolean weakBeforeColor = weakDto.opponents().get(weakKeySet[weakKeySet.length - 2]).color();

        if (lastColor == weakLastColor) {
            if (beforeColor == weakBeforeColor) {
                return !lastColor;
            } else {
                return !(lastColor == beforeColor);
            }
        }
        return !lastColor;
    }

    private void updateRating(TournmentDto tournment) {
        if (tournment.players() == null) return;

        for (PlayerDto playerDto : tournment.players()) {
            if (playerDto.opponents() == null) continue;

            int finalPoints = 0;
            int basePoints = 8;

            for (String name : playerDto.opponents().keySet()) {
                ResultDto resultDto = playerDto.opponents().get(name);
                if (resultDto == null || resultDto.playerDto() == null) continue;

                int ratingDiff = resultDto.playerDto().rating() - playerDto.rating();
                int adjustment = Math.min(basePoints + (Math.abs(ratingDiff) % 20), 15);

                if (resultDto.win()) {
                    if (ratingDiff >= 0) {
                        finalPoints += adjustment;
                    } else {
                        finalPoints -= adjustment;
                    }
                } else {
                    if (ratingDiff >= 0) {
                        finalPoints -= adjustment;
                    } else {
                        finalPoints += adjustment;
                    }
                }
                playerService.update(playerDto);
            }
        }
    }

    public PlayerDto getPlayerById(int id) {
        return playerService.selectById(id);
    }

    public PlayerDto getPlayerData(Integer tournamentId, Integer playerId) {
        PlayerDto player = playerService.selectById(playerId);
        List<MatchEntity> matches = matchService.getMatchesForPlayer(playerId, tournamentId);
        Map<String, ResultDto> opponentsMap = new HashMap<>();

        for (MatchEntity match : matches) {
            boolean isPlayer1 = match.getPlayer1().getId().equals(playerId);
            PlayerEntity opponentEntity = isPlayer1 ? match.getPlayer2() : match.getPlayer1();

            if (opponentEntity == null || opponentEntity.getName().equals("BYE")) continue;

            boolean color = isPlayer1 ? match.isColorPlayer1() : !match.isColorPlayer1();

            boolean win;
            if (match.isWinPlayer1() && isPlayer1) {
                win = true;
            } else if (!match.isWinPlayer1() && !isPlayer1) {
                win = true;
            } else {
                win = false;
            }

            PlayerDto opponentDto = new PlayerDto(
                    opponentEntity.getId(),
                    opponentEntity.getName(),
                    opponentEntity.getRating(),
                    opponentEntity.getScore(),
                    opponentEntity.isByeReceived(),
                    null
            );

            ResultDto resultDto = new ResultDto(opponentDto, color, win);
            opponentsMap.put(opponentEntity.getName(), resultDto);
        }

        return new PlayerDto(
                player.id(),
                player.name(),
                player.rating(),
                player.score(),
                player.byeReceived(),
                opponentsMap
        );
    }
}