package com.domingol.engine.core;

import com.domingol.engine.entities.Player;
import com.domingol.engine.entities.Team;
import com.domingol.engine.spatial.Vector2D;

import java.util.List;

/**
 * Classic 4-4-2 formation.
 * <p>
 * Structure:
 * - 1 Goalkeeper
 * - 4 Defenders (LB, CB, CB, RB)
 * - 4 Midfielders (LM, CM, CM, RM)
 * - 2 Forwards (LW/ST, RW/ST)
 * <p>
 * Positions are mirrored for home (attacking right) vs away (attacking left).
 */
public class Formation442 implements Formation {

    @Override
    public String getName() {
        return "4-4-2";
    }

    @Override
    public void apply(Team team, boolean attackingRight) {
        List<Player> players = team.getPlayers();

        if (players.size() != 11) {
            throw new IllegalArgumentException("Formation requires exactly 11 players");
        }

        // Get players by position
        Player gk = team.getGoalkeeper();
        List<Player> defenders = team.getDefenders();
        List<Player> midfielders = team.getMidfielders();
        List<Player> attackers = team.getAttackers();

        // Validate squad composition
        if (defenders.size() < 4) {
            throw new IllegalArgumentException("4-4-2 requires at least 4 defenders");
        }
        if (midfielders.size() < 4) {
            throw new IllegalArgumentException("4-4-2 requires at least 4 midfielders");
        }
        if (attackers.size() < 2) {
            throw new IllegalArgumentException("4-4-2 requires at least 2 attackers");
        }

        if (attackingRight) {
            applyAttackingRight(gk, defenders, midfielders, attackers);
        } else {
            applyAttackingLeft(gk, defenders, midfielders, attackers);
        }
    }

    private void applyAttackingRight(
            Player gk,
            List<Player> defenders,
            List<Player> midfielders,
            List<Player> attackers) {

        // Goalkeeper
        gk.setFieldPosition(new Vector2D(5, 34));

        // Defenders (4): LB, CB, CB, RB
        defenders.get(0).setFieldPosition(new Vector2D(15, 15));   // LB
        defenders.get(1).setFieldPosition(new Vector2D(15, 27));   // CB
        defenders.get(2).setFieldPosition(new Vector2D(15, 41));   // CB
        defenders.get(3).setFieldPosition(new Vector2D(15, 53));   // RB

        // Midfielders (4): LM, CM, CM, RM
        midfielders.get(0).setFieldPosition(new Vector2D(35, 12));  // LM
        midfielders.get(1).setFieldPosition(new Vector2D(40, 27));  // CM
        midfielders.get(2).setFieldPosition(new Vector2D(40, 41));  // CM
        midfielders.get(3).setFieldPosition(new Vector2D(35, 56));  // RM

        // Forwards (2): MOVED CLOSER TO GOAL
        attackers.get(0).setFieldPosition(new Vector2D(80, 24));   // ← CHANGED from 65
        attackers.get(1).setFieldPosition(new Vector2D(80, 44));   // ← CHANGED from 65
        // Distance to goal (105, 34): ~25m ✅
    }

    private void applyAttackingLeft(
            Player gk,
            List<Player> defenders,
            List<Player> midfielders,
            List<Player> attackers) {

        // Goalkeeper
        gk.setFieldPosition(new Vector2D(100, 34));

        // Defenders (4): RB, CB, CB, LB
        defenders.get(0).setFieldPosition(new Vector2D(90, 53));   // RB
        defenders.get(1).setFieldPosition(new Vector2D(90, 41));   // CB
        defenders.get(2).setFieldPosition(new Vector2D(90, 27));   // CB
        defenders.get(3).setFieldPosition(new Vector2D(90, 15));   // LB

        // Midfielders (4): RM, CM, CM, LM
        midfielders.get(0).setFieldPosition(new Vector2D(70, 56));  // RM
        midfielders.get(1).setFieldPosition(new Vector2D(65, 41));  // CM
        midfielders.get(2).setFieldPosition(new Vector2D(65, 27));  // CM
        midfielders.get(3).setFieldPosition(new Vector2D(70, 12));  // LM

        // Forwards (2): MOVED CLOSER TO GOAL
        attackers.get(0).setFieldPosition(new Vector2D(25, 44));   // ← CHANGED from 40
        attackers.get(1).setFieldPosition(new Vector2D(25, 24));   // ← CHANGED from 40
        // Distance to goal (0, 34): ~25m ✅
    }
}
