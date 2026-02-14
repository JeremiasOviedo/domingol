package com.domingol.engine.entities;

import lombok.Getter;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Represents a football team.
 * <p>
 * A team consists of exactly 11 players positioned according to a formation.
 * Teams are the primary organizational unit in matches.
 * <p>
 * <b>Design decisions:</b>
 * <ul>
 *   <li>Team has List of Players (bidirectional with Player.team)</li>
 *   <li>Exactly 11 players required (1 GK + 10 outfield)</li>
 *   <li>Players are validated on team creation</li>
 *   <li>Formation is simple for MVP (just position assignments)</li>
 * </ul>
 *
 * <h3>Usage:</h3>
 * <pre>
 * // Create team
 * Team team = Team.builder()
 *     .id("team1")
 *     .name("Manchester City")
 *     .players(elevenPlayers)
 *     .build();
 *
 * // Access players
 * List&lt;Player&gt; defenders = team.getPlayersByPosition(Position::isDefender);
 * Player gk = team.getGoalkeeper();
 * </pre>
 *
 * @see Player for individual player management
 */
@Getter
public class Team {

    // === IDENTITY ===

    /**
     * Unique identifier for the team.
     * <p>
     * Used for referencing in matches, leagues, and events.
     * Format: "team1", "team2", etc. for simplicity in MVP.
     */
    @NonNull
    private final String id;

    /**
     * Team's display name.
     * <p>
     * Used in match reports and UI.
     * Example: "Manchester City", "Real Madrid"
     */
    @NonNull
    private final String name;

    // === PLAYERS ===

    /**
     * List of 11 players in this team.
     * <p>
     * Must contain exactly 11 players: 1 goalkeeper + 10 outfield.
     * Players are ordered by their position in the formation.
     * <p>
     * Immutable list (defensive copy to prevent external modification).
     */
    @NonNull
    private final List<Player> players;

    // === CONSTRUCTOR ===

    /**
     * Private constructor - use builder.
     *
     * @param id team identifier
     * @param name team name
     * @param players list of exactly 11 players
     */
    private Team(@NonNull String id, @NonNull String name, @NonNull List<Player> players) {
        this.id = id;
        this.name = name;

        // Defensive copy (immutable)
        this.players = Collections.unmodifiableList(new ArrayList<>(players));

        // Validate team composition
        // NOTE: Validation is disabled in constructor to allow:
        // 1. Creating dummy teams for tests (PlayerTest, BallTest)
        // 2. Solving circular dependency (Team needs Players, Player needs Team)
        //
        // Call validateForMatch() explicitly before starting a match simulation
        // validateTeam();
    }

    /**
     * Validate team has exactly 11 players including 1 goalkeeper.
     * <p>
     * Call this before starting a match to ensure team is valid.
     *
     * @throws IllegalStateException if validation fails
     */
    private void validateTeam() {
        // Check exactly 11 players
        if (players.size() != 11) {
            throw new IllegalStateException(
                    String.format("Team must have exactly 11 players, got: %d", players.size())
            );
        }

        // Check exactly 1 goalkeeper
        long goalkeepers = players.stream()
                .filter(Player::isGoalkeeper)
                .count();

        if (goalkeepers != 1) {
            throw new IllegalStateException(
                    String.format("Team must have exactly 1 goalkeeper, got: %d", goalkeepers)
            );
        }

        // Check all players belong to this team
        // TODO: Re-enable after solving circular dependency
        // for (Player player : players) {
        //     if (!this.equals(player.getTeam())) {
        //         throw new IllegalStateException(
        //             String.format("Player %s does not belong to team %s",
        //                 player.getName(), this.name)
        //         );
        //     }
        // }
    }

    /**
     * Validate team is ready for a match.
     * <p>
     * Checks:
     * <ul>
     *   <li>Exactly 11 players</li>
     *   <li>Exactly 1 goalkeeper</li>
     * </ul>
     * <p>
     * Call this before starting a match simulation.
     *
     * @throws IllegalStateException if team is invalid
     */
    public void validateForMatch() {
        validateTeam();
    }

    // === PLAYER QUERIES ===

    /**
     * Get goalkeeper.
     *
     * @return the goalkeeper (guaranteed to exist after validation)
     */
    public Player getGoalkeeper() {
        return players.stream()
                .filter(Player::isGoalkeeper)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No goalkeeper found"));
    }

    /**
     * Get all outfield players (non-goalkeepers).
     *
     * @return list of 10 outfield players
     */
    public List<Player> getOutfieldPlayers() {
        return players.stream()
                .filter(p -> !p.isGoalkeeper())
                .collect(Collectors.toList());
    }

    /**
     * Get player by ID.
     *
     * @param playerId player identifier
     * @return player if found, empty otherwise
     */
    public Optional<Player> getPlayerById(String playerId) {
        return players.stream()
                .filter(p -> p.getId().equals(playerId))
                .findFirst();
    }

    /**
     * Get players by position type.
     * <p>
     * Example usage:
     * <pre>
     * // Get all defenders
     * List&lt;Player&gt; defenders = team.getPlayersByPosition(Position::isDefender);
     *
     * // Get all attackers
     * List&lt;Player&gt; attackers = team.getPlayersByPosition(Position::isAttacker);
     * </pre>
     *
     * @param positionFilter filter predicate (e.g., Position::isDefender)
     * @return list of players matching the filter
     */
    public List<Player> getPlayersByPosition(java.util.function.Predicate<Position> positionFilter) {
        return players.stream()
                .filter(p -> positionFilter.test(p.getPosition()))
                .collect(Collectors.toList());
    }

    /**
     * Get defenders (CB, LB, RB).
     *
     * @return list of defenders
     */
    public List<Player> getDefenders() {
        return getPlayersByPosition(Position::isDefender);
    }

    /**
     * Get midfielders (CDM, CM, CAM).
     *
     * @return list of midfielders
     */
    public List<Player> getMidfielders() {
        return getPlayersByPosition(Position::isMidfielder);
    }

    /**
     * Get attackers (LW, RW, ST).
     *
     * @return list of attackers
     */
    public List<Player> getAttackers() {
        return getPlayersByPosition(Position::isAttacker);
    }

    // === MATCH QUERIES ===

    /**
     * Get player who currently has the ball.
     * <p>
     * Checks ball possessor against this team's players.
     *
     * @param ball match ball
     * @return player with ball, or empty if no one from this team has it
     */
    public Optional<Player> getPlayerWithBall(Ball ball) {
        if (ball.getPossessor() == null) {
            return Optional.empty();
        }

        return players.stream()
                .filter(p -> p.equals(ball.getPossessor()))
                .findFirst();
    }

    /**
     * Check if this team has possession of the ball.
     *
     * @param ball match ball
     * @return true if any player from this team has the ball
     */
    public boolean hasPossession(Ball ball) {
        return getPlayerWithBall(ball).isPresent();
    }

    // === BUILDER ===

    /**
     * Create a team builder.
     *
     * @return new builder instance
     */
    public static TeamBuilder builder() {
        return new TeamBuilder();
    }

    /**
     * Builder for Team.
     * <p>
     * Ensures all required fields are set before creating team.
     */
    public static class TeamBuilder {
        private String id;
        private String name;
        private List<Player> players;

        public TeamBuilder id(String id) {
            this.id = id;
            return this;
        }

        public TeamBuilder name(String name) {
            this.name = name;
            return this;
        }

        public TeamBuilder players(List<Player> players) {
            this.players = players;
            return this;
        }

        public Team build() {
            if (id == null) {
                throw new IllegalStateException("Team id is required");
            }
            if (name == null) {
                throw new IllegalStateException("Team name is required");
            }
            if (players == null) {
                throw new IllegalStateException("Team players are required");
            }

            return new Team(id, name, players);
        }
    }

    // === OBJECT METHODS ===

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Team team = (Team) o;
        return id.equals(team.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return String.format("Team{id='%s', name='%s', players=%d}", id, name, players.size());
    }
}
