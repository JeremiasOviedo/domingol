package com.domingol.engine.core;

import com.domingol.engine.actions.PassAction;
import com.domingol.engine.actions.ShootAction;
import com.domingol.engine.entities.Ball;
import com.domingol.engine.entities.Player;
import com.domingol.engine.entities.Team;
import com.domingol.engine.events.Event;
import com.domingol.engine.spatial.SpatialGrid;
import com.domingol.engine.spatial.Vector2D;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Core match simulation engine.
 */
public class SimulationEngine {

    public MatchResult simulate(Team homeTeam, Team awayTeam, long seed) {
        homeTeam.validateForMatch();
        awayTeam.validateForMatch();

        GameState state = initializeMatch(homeTeam, awayTeam);
        Random rng = new Random(seed);
        EventLogger logger = new EventLogger();

        SpatialGrid spatialGrid = new SpatialGrid(
                FieldConstants.FIELD_WIDTH,
                FieldConstants.FIELD_LENGTH,
                5.0  // 5m cell size
        );

        int homePosTicks = 0;
        int awayPosTicks = 0;
        int homeShotAttempts = 0;
        int awayShotAttempts = 0;

        // Main simulation loop
        while (!state.isMatchOver()) {
            if (state.getPhase() == GameState.MatchPhase.HALF_TIME) {
                state.advanceTick();
                continue;
            }

            // 1. Update physics
            updatePhysics(state);

            // 2. Update spatial grid with current positions
            updateSpatialGrid(spatialGrid, state);

            // 3. Check for loose ball
            checkLooseBall(state, rng);

            if (state.getHomeTeam().hasPossession(state.getBall())) {
                homePosTicks++;
            } else if (state.getAwayTeam().hasPossession(state.getBall())) {
                awayPosTicks++;
            }

// Count shot attempts (before executing actions)


            // 4. AI decisions (now with spatial awareness)
            List<Action> actions = makeDecisions(state, rng, spatialGrid);

            for (Action action : actions) {
                if (action instanceof ShootAction shootAction) {
                    if (shootAction.getShooter().getTeam() == state.getHomeTeam()) {
                        homeShotAttempts++;
                    } else {
                        awayShotAttempts++;
                    }
                }
            }

            // 5. Execute actions
            for (Action action : actions) {
                Event event = action.execute(state, rng);
                logger.log(event);
            }

            // 6. Advance time
            state.advanceTick();
        }

        System.out.println("\n=== DEBUG STATS ===");
        System.out.printf("Possession: Home %d ticks (%.1f%%), Away %d ticks (%.1f%%)%n",
                homePosTicks, homePosTicks * 100.0 / 5280,
                awayPosTicks, awayPosTicks * 100.0 / 5280);
        System.out.printf("Shot attempts: Home %d, Away %d%n",
                homeShotAttempts, awayShotAttempts);
        System.out.printf("Goals: Home %d, Away %d%n",
                state.getHomeScore(), state.getAwayScore());
        System.out.printf("Conversion rate: Home %.1f%%, Away %.1f%%%n",
                state.getHomeScore() * 100.0 / Math.max(homeShotAttempts, 1),
                state.getAwayScore() * 100.0 / Math.max(awayShotAttempts, 1));

        return new MatchResult(state, logger.getEvents());
    }

    private GameState initializeMatch(Team homeTeam, Team awayTeam) {
        Ball ball = new Ball(FieldConstants.FIELD_CENTER);

        Formation formation = new Formation442();
        formation.apply(homeTeam, true);
        formation.apply(awayTeam, false);

        System.out.println();

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

    private List<Action> makeDecisions(GameState state, Random rng, SpatialGrid grid) {
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

        boolean shouldShoot = shouldShoot(playerWithBall, teamWithBall, state, rng);

        if (shouldShoot) {
            actions.add(new ShootAction(playerWithBall));
        } else {
            // NEW: Pass grid to findPassTarget
            Player receiver = findPassTarget(playerWithBall, teamWithBall, rng, grid, state);
            if (receiver != null) {
                actions.add(new PassAction(playerWithBall, receiver));
            }
        }

        return actions;
    }

    /**
     * Decides if player should shoot.
     * <p>
     * Checks:
     * - Player is attacker
     * - Distance to goal < 30m
     * - Random roll (5%)
     */
    private boolean shouldShoot(Player player, Team team, GameState state, Random rng) {
        if (!player.getPosition().isAttacker()) {
            return false;
        }

        // Determine which goal to shoot at
        Vector2D targetGoal;
        if (team == state.getHomeTeam()) {
            targetGoal = FieldConstants.AWAY_GOAL_CENTER;
        } else {
            targetGoal = FieldConstants.HOME_GOAL_CENTER;
        }

        // Check distance to goal
        double distance = player.getFieldPosition().distanceTo(targetGoal);

        if (distance > 30.0) {
            return false;  // Too far to shoot
        }

        // Random decision (5% of possessions within range)
        return rng.nextDouble() < 0.05;
    }

    /**
     * Finds best pass target using spatial awareness.
     * <p>
     * Considers:
     * - Distance to passer (prefer closer)
     * - Marking pressure (prefer unmarked)
     * <p>
     * MVP: Simple scoring system
     * Post-MVP: Consider pass lane obstacles, offside, etc.
     */
    private Player findPassTarget(Player passer, Team team, Random rng,
                                  SpatialGrid grid, GameState state) {
        List<Player> teammates = new ArrayList<>(team.getPlayers());
        teammates.remove(passer);

        if (teammates.isEmpty()) {
            return null;
        }

        Set<String> nearbyEntityIds = grid.getEntitiesNear(
                passer.getFieldPosition(),
                30.0
        );

        Team opponentTeam = (team == state.getHomeTeam())
                ? state.getAwayTeam()
                : state.getHomeTeam();

        List<Player> viableTargets = teammates.stream()
                .filter(t -> nearbyEntityIds.contains(t.getId()))
                .filter(t -> t.getFieldPosition().distanceTo(passer.getFieldPosition()) <= 30.0)
                .toList();

        if (viableTargets.isEmpty()) {
            return teammates.get(rng.nextInt(teammates.size()));
        }

        Player best = null;
        double bestScore = -1;

        for (Player target : viableTargets) {
            double score = evaluatePassTarget(target, passer, grid, opponentTeam,team,state);

            if (score > bestScore) {
                bestScore = score;
                best = target;
            }
        }

        return best != null ? best : viableTargets.get(0);
    }

    /**
     * Evaluates how good a pass target is.
     * Higher score = better target.
     */
    private double evaluatePassTarget(Player target, Player passer,
                                      SpatialGrid grid, Team opponentTeam,
                                      Team playerTeam, GameState state) {
        double score = 0;

        double distance = target.getFieldPosition().distanceTo(passer.getFieldPosition());

        double distanceScore = Math.max(0, (30.0 - distance) / 30.0);
        score += distanceScore;

        Vector2D targetGoal = (playerTeam == state.getHomeTeam())
                ? FieldConstants.AWAY_GOAL_CENTER
                : FieldConstants.HOME_GOAL_CENTER;

        double passerDistToGoal = passer.getFieldPosition().distanceTo(targetGoal);
        double targetDistToGoal = target.getFieldPosition().distanceTo(targetGoal);

        if (targetDistToGoal < passerDistToGoal) {
            score += 0.8;
        }

        Set<String> nearTarget = grid.getEntitiesNear(target.getFieldPosition(), 3.0);
        boolean isMarked = opponentTeam.getPlayers().stream()
                .anyMatch(opponent -> nearTarget.contains(opponent.getId()));

        if (!isMarked) {
            score += 1.0;
        }

        return score;
    }

    /**
     * Updates spatial grid with current player positions.
     * Called every tick to keep spatial queries accurate.
     */
    private void updateSpatialGrid(SpatialGrid grid, GameState state) {
        grid.clear();

        // Insert all home team players
        for (Player p : state.getHomeTeam().getPlayers()) {
            grid.insert(p.getId(), p.getFieldPosition());
        }

        // Insert all away team players
        for (Player p : state.getAwayTeam().getPlayers()) {
            grid.insert(p.getId(), p.getFieldPosition());
        }
    }
}

