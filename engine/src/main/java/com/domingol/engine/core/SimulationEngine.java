package com.domingol.engine.core;

import com.domingol.engine.actions.PassAction;
import com.domingol.engine.actions.ShootAction;
import com.domingol.engine.entities.Ball;
import com.domingol.engine.entities.Player;
import com.domingol.engine.entities.Team;
import com.domingol.engine.events.Event;
import com.domingol.engine.spatial.Vector2D;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Core match simulation engine.
 */
public class SimulationEngine {

    private static final Vector2D FIELD_CENTER = new Vector2D(52.5, 34.0);

    public MatchResult simulate(Team homeTeam, Team awayTeam, long seed) {
        homeTeam.validateForMatch();
        awayTeam.validateForMatch();

        GameState state = initializeMatch(homeTeam, awayTeam);
        Random rng = new Random(seed);
        EventLogger logger = new EventLogger();

        // Main simulation loop
        while (!state.isMatchOver()) {
            if (state.getPhase() == GameState.MatchPhase.HALF_TIME) {
                state.advanceTick();
                continue;
            }

            // 1. Update physics
            updatePhysics(state);

            // 2. Check for loose ball
            checkLooseBall(state, rng);

            // 3. AI decisions
            List<Action> actions = makeDecisions(state, rng);

            // 4. Execute actions
            for (Action action : actions) {
                Event event = action.execute(state, rng);
                logger.log(event);
            }

            // 5. Advance time
            state.advanceTick();
        }

        return new MatchResult(state, logger.getEvents());
    }

    private GameState initializeMatch(Team homeTeam, Team awayTeam) {
        Ball ball = new Ball(FIELD_CENTER);

        // Give ball to home midfielder for kickoff
        Player kickoffPlayer = homeTeam.getPlayers().get(5);
        ball.setPossessor(kickoffPlayer);

        return new GameState(homeTeam, awayTeam, ball);
    }

    private void updatePhysics(GameState state) {
        Ball ball = state.getBall();

        if (ball.getPossessor() != null) {
            ball.setPosition(ball.getPossessor().getFieldPosition());
        }
    }

    /**
     * Assigns possession when ball is loose.
     * MVP: Random team gets it.
     */
    private void checkLooseBall(GameState state, Random rng) {
        Ball ball = state.getBall();

        if (ball.getPossessor() != null) {
            return;
        }

        // Ball is loose - assign to random team
        Team teamToGetBall = rng.nextBoolean()
                ? state.getHomeTeam()
                : state.getAwayTeam();

        // Give to random player
        List<Player> players = teamToGetBall.getPlayers();
        Player newPossessor = players.get(rng.nextInt(players.size()));
        ball.setPossessor(newPossessor);
    }

    private List<Action> makeDecisions(GameState state, Random rng) {
        List<Action> actions = new ArrayList<>();

        Ball ball = state.getBall();
        Team homeTeam = state.getHomeTeam();
        Team awayTeam = state.getAwayTeam();

        Player playerWithBall = null;
        Team teamWithBall = null;

        if (homeTeam.hasPossession(ball)) {
            playerWithBall = homeTeam.getPlayerWithBall(ball).orElse(null);
            teamWithBall = homeTeam;
        } else if (awayTeam.hasPossession(ball)) {
            playerWithBall = awayTeam.getPlayerWithBall(ball).orElse(null);
            teamWithBall = awayTeam;
        }

        if (playerWithBall == null) {
            return actions;
        }

        boolean shouldShoot = shouldShoot(playerWithBall, teamWithBall, state);

        if (shouldShoot) {
            actions.add(new ShootAction(playerWithBall));
        } else {
            Player receiver = findPassTarget(playerWithBall, teamWithBall, rng);
            if (receiver != null) {
                actions.add(new PassAction(playerWithBall, receiver));
            }
        }

        return actions;
    }

    private boolean shouldShoot(Player player, Team team, GameState state) {
        return player.getPosition().isAttacker();
    }

    private Player findPassTarget(Player passer, Team team, Random rng) {
        List<Player> teammates = new ArrayList<>(team.getPlayers());
        teammates.remove(passer);

        if (teammates.isEmpty()) {
            return null;
        }

        return teammates.get(rng.nextInt(teammates.size()));
    }
}

