package net.minecraft.scoreboard;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class Scoreboard {
    private final Map<String, ScoreObjective> scoreObjectives = Maps.newHashMap();
    private final Map<IScoreObjectiveCriteria, List<ScoreObjective>> scoreObjectiveCriterias = Maps.newHashMap();
    private final Map<String, Map<ScoreObjective, Score>> entitiesScoreObjectives = Maps.newHashMap();

    /**
     * Index 0 is tab menu, 1 is sidebar, and 2 is below name
     */
    private final ScoreObjective[] objectiveDisplaySlots = new ScoreObjective[19];
    private final Map<String, ScorePlayerTeam> teams = Maps.newHashMap();
    private final Map<String, ScorePlayerTeam> teamMemberships = Maps.newHashMap();
    private static String[] field_178823_g = null;

    /**
     * Returns a ScoreObjective for the objective name
     *
     * @param name The objective name
     */
    public ScoreObjective getObjective(final String name) {
        return this.scoreObjectives.get(name);
    }

    /**
     * Create and returns the score objective for the given name and ScoreCriteria
     *
     * @param name     The ScoreObjective Name
     * @param criteria The ScoreObjective Criteria
     */
    public ScoreObjective addScoreObjective(final String name, final IScoreObjectiveCriteria criteria) {
        if (name.length() > 16) {
            throw new IllegalArgumentException("The objective name '" + name + "' is too long!");
        } else {
            ScoreObjective scoreobjective = this.getObjective(name);

            if (scoreobjective != null) {
                throw new IllegalArgumentException("An objective with the name '" + name + "' already exists!");
            } else {
                scoreobjective = new ScoreObjective(this, name, criteria);
                List<ScoreObjective> list = this.scoreObjectiveCriterias.get(criteria);

                if (list == null) {
                    list = Lists.newArrayList();
                    this.scoreObjectiveCriterias.put(criteria, list);
                }

                list.add(scoreobjective);
                this.scoreObjectives.put(name, scoreobjective);
                this.onScoreObjectiveAdded(scoreobjective);
                return scoreobjective;
            }
        }
    }

    public Collection<ScoreObjective> getObjectivesFromCriteria(final IScoreObjectiveCriteria criteria) {
        final Collection<ScoreObjective> collection = this.scoreObjectiveCriterias.get(criteria);
        return collection == null ? Lists.newArrayList() : Lists.newArrayList(collection);
    }

    /**
     * Returns if the entity has the given ScoreObjective
     *
     * @param name The Entity name
     */
    public boolean entityHasObjective(final String name, final ScoreObjective p_178819_2_) {
        final Map<ScoreObjective, Score> map = this.entitiesScoreObjectives.get(name);

        if (map == null) {
            return false;
        } else {
            final Score score = map.get(p_178819_2_);
            return score != null;
        }
    }

    /**
     * Returns the value of the given objective for the given entity name
     *
     * @param name      The entity name
     * @param objective The ScoreObjective to get the value from
     */
    public Score getValueFromObjective(final String name, final ScoreObjective objective) {
        if (name.length() > 40) {
            throw new IllegalArgumentException("The player name '" + name + "' is too long!");
        } else {
            Map<ScoreObjective, Score> map = this.entitiesScoreObjectives.get(name);

            if (map == null) {
                map = Maps.newHashMap();
                this.entitiesScoreObjectives.put(name, map);
            }

            Score score = map.get(objective);

            if (score == null) {
                score = new Score(this, objective, name);
                map.put(objective, score);
            }

            return score;
        }
    }

    public Collection<Score> getSortedScores(final ScoreObjective objective) {
        List<Score> list = Lists.newArrayList();

        for (Map<ScoreObjective, Score> map : this.entitiesScoreObjectives.values()) {
            Score score = map.get(objective);

            if (score != null) {
                list.add(score);
            }
        }

        list.sort(Score.scoreComparator);
        return list;
    }

    public Collection<ScoreObjective> getScoreObjectives() {
        return this.scoreObjectives.values();
    }

    public Collection<String> getObjectiveNames() {
        return this.entitiesScoreObjectives.keySet();
    }

    /**
     * Remove the given ScoreObjective for the given Entity name.
     *
     * @param name      The entity Name
     * @param objective The ScoreObjective
     */
    public void removeObjectiveFromEntity(final String name, final ScoreObjective objective) {
        if (objective == null) {
            final Map<ScoreObjective, Score> map = this.entitiesScoreObjectives.remove(name);

            if (map != null) {
                this.func_96516_a(name);
            }
        } else {
            final Map<ScoreObjective, Score> map2 = this.entitiesScoreObjectives.get(name);

            if (map2 != null) {
                final Score score = map2.remove(objective);

                if (map2.size() < 1) {
                    final Map<ScoreObjective, Score> map1 = this.entitiesScoreObjectives.remove(name);

                    if (map1 != null) {
                        this.func_96516_a(name);
                    }
                } else if (score != null) {
                    this.func_178820_a(name, objective);
                }
            }
        }
    }

    public Collection<Score> getScores() {
        final Collection<Map<ScoreObjective, Score>> collection = this.entitiesScoreObjectives.values();
        final List<Score> list = Lists.newArrayList();

        for (final Map<ScoreObjective, Score> map : collection) {
            list.addAll(map.values());
        }

        return list;
    }

    public Map<ScoreObjective, Score> getObjectivesForEntity(final String name) {
        Map<ScoreObjective, Score> map = this.entitiesScoreObjectives.get(name);

        if (map == null) {
            map = Maps.newHashMap();
        }

        return map;
    }

    public void removeObjective(final ScoreObjective p_96519_1_) {
        if (p_96519_1_ == null || this.scoreObjectives == null) return;
        this.scoreObjectives.remove(p_96519_1_.getName());

        for (int i = 0; i < 19; ++i) {
            if (this.getObjectiveInDisplaySlot(i) == p_96519_1_) {
                this.setObjectiveInDisplaySlot(i, null);
            }
        }

        final List<ScoreObjective> list = this.scoreObjectiveCriterias.get(p_96519_1_.getCriteria());

        if (list != null) {
            list.remove(p_96519_1_);
        }

        for (final Map<ScoreObjective, Score> map : this.entitiesScoreObjectives.values()) {
            map.remove(p_96519_1_);
        }

        this.func_96533_c(p_96519_1_);
    }

    /**
     * 0 is tab menu, 1 is sidebar, 2 is below name
     */
    public void setObjectiveInDisplaySlot(final int p_96530_1_, final ScoreObjective p_96530_2_) {
        this.objectiveDisplaySlots[p_96530_1_] = p_96530_2_;
    }

    /**
     * 0 is tab menu, 1 is sidebar, 2 is below name
     */
    public ScoreObjective getObjectiveInDisplaySlot(final int p_96539_1_) {
        return this.objectiveDisplaySlots[p_96539_1_];
    }

    /**
     * Retrieve the ScorePlayerTeam instance identified by the passed team name
     */
    public ScorePlayerTeam getTeam(final String p_96508_1_) {
        return this.teams.get(p_96508_1_);
    }

    public ScorePlayerTeam createTeam(final String p_96527_1_) {
        if (p_96527_1_.length() > 16) {
            throw new IllegalArgumentException("The team name '" + p_96527_1_ + "' is too long!");
        } else {
            ScorePlayerTeam scoreplayerteam = this.getTeam(p_96527_1_);

            if (scoreplayerteam != null) {
                throw new IllegalArgumentException("A team with the name '" + p_96527_1_ + "' already exists!");
            } else {
                scoreplayerteam = new ScorePlayerTeam(this, p_96527_1_);
                this.teams.put(p_96527_1_, scoreplayerteam);
                this.broadcastTeamCreated(scoreplayerteam);
                return scoreplayerteam;
            }
        }
    }

    /**
     * Removes the team from the scoreboard, updates all player memberships and broadcasts the deletion to all players
     */
    public void removeTeam(final ScorePlayerTeam p_96511_1_) {
        if (p_96511_1_ == null) return;
        this.teams.remove(p_96511_1_.getRegisteredName());

        for (final String s : p_96511_1_.getMembershipCollection()) {
            this.teamMemberships.remove(s);
        }

        this.func_96513_c(p_96511_1_);
    }

    /**
     * Adds a player to the given team
     *
     * @param player  The name of the player to add
     * @param newTeam The name of the team
     */
    public boolean addPlayerToTeam(final String player, final String newTeam) {
        if (player.length() > 40) {
            throw new IllegalArgumentException("The player name '" + player + "' is too long!");
        } else if (!this.teams.containsKey(newTeam)) {
            return false;
        } else {
            final ScorePlayerTeam scoreplayerteam = this.getTeam(newTeam);

            if (this.getPlayersTeam(player) != null) {
                this.removePlayerFromTeams(player);
            }

            this.teamMemberships.put(player, scoreplayerteam);
            scoreplayerteam.getMembershipCollection().add(player);
            return true;
        }
    }

    public boolean removePlayerFromTeams(final String p_96524_1_) {
        final ScorePlayerTeam scoreplayerteam = this.getPlayersTeam(p_96524_1_);

        if (scoreplayerteam != null) {
            this.removePlayerFromTeam(p_96524_1_, scoreplayerteam);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Removes the given username from the given ScorePlayerTeam. If the player is not on the team then an
     * IllegalStateException is thrown.
     */
    public void removePlayerFromTeam(final String p_96512_1_, final ScorePlayerTeam p_96512_2_) {
        if (this.getPlayersTeam(p_96512_1_) != p_96512_2_) {
            System.out.println("Scoreboard Failed to remove player, loc: Scoreboard.java func: removePlayerFromTeam line: 307 (\"Player is either on another team or not on any team. Cannot remove from team '\" + p_96512_2_.getRegisteredName() + \"'.\")");
        } else {
            this.teamMemberships.remove(p_96512_1_);
            p_96512_2_.getMembershipCollection().remove(p_96512_1_);
        }
    }

    public Collection<String> getTeamNames() {
        return this.teams.keySet();
    }

    public Collection<ScorePlayerTeam> getTeams() {
        return this.teams.values();
    }

    /**
     * Gets the ScorePlayerTeam object for the given username.
     */
    public ScorePlayerTeam getPlayersTeam(final String p_96509_1_) {
        return this.teamMemberships.get(p_96509_1_);
    }

    /**
     * Called when a score objective is added
     */
    public void onScoreObjectiveAdded(final ScoreObjective scoreObjectiveIn) {
    }

    public void func_96532_b(final ScoreObjective p_96532_1_) {
    }

    public void func_96533_c(final ScoreObjective p_96533_1_) {
    }

    public void func_96536_a(final Score p_96536_1_) {
    }

    public void func_96516_a(final String p_96516_1_) {
    }

    public void func_178820_a(final String p_178820_1_, final ScoreObjective p_178820_2_) {
    }

    /**
     * This packet will notify the players that this team is created, and that will register it on the client
     */
    public void broadcastTeamCreated(final ScorePlayerTeam playerTeam) {
    }

    /**
     * This packet will notify the players that this team is updated
     */
    public void sendTeamUpdate(final ScorePlayerTeam playerTeam) {
    }

    public void func_96513_c(final ScorePlayerTeam playerTeam) {
    }

    /**
     * Returns 'list' for 0, 'sidebar' for 1, 'belowName for 2, otherwise null.
     */
    public static String getObjectiveDisplaySlot(final int p_96517_0_) {
        switch (p_96517_0_) {
            case 0:
                return "list";

            case 1:
                return "sidebar";

            case 2:
                return "belowName";

            default:
                if (p_96517_0_ >= 3 && p_96517_0_ <= 18) {
                    final EnumChatFormatting enumchatformatting = EnumChatFormatting.func_175744_a(p_96517_0_ - 3);

                    if (enumchatformatting != null && enumchatformatting != EnumChatFormatting.RESET) {
                        return "sidebar.team." + enumchatformatting.getFriendlyName();
                    }
                }

                return null;
        }
    }

    /**
     * Returns 0 for (case-insensitive) 'list', 1 for 'sidebar', 2 for 'belowName', otherwise -1.
     */
    public static int getObjectiveDisplaySlotNumber(final String p_96537_0_) {
        if (p_96537_0_.equalsIgnoreCase("list")) {
            return 0;
        } else if (p_96537_0_.equalsIgnoreCase("sidebar")) {
            return 1;
        } else if (p_96537_0_.equalsIgnoreCase("belowName")) {
            return 2;
        } else {
            if (p_96537_0_.startsWith("sidebar.team.")) {
                final String s = p_96537_0_.substring("sidebar.team.".length());
                final EnumChatFormatting enumchatformatting = EnumChatFormatting.getValueByName(s);

                if (enumchatformatting != null && enumchatformatting.getColorIndex() >= 0) {
                    return enumchatformatting.getColorIndex() + 3;
                }
            }

            return -1;
        }
    }

    public static String[] getDisplaySlotStrings() {
        if (field_178823_g == null) {
            field_178823_g = new String[19];

            for (int i = 0; i < 19; ++i) {
                field_178823_g[i] = getObjectiveDisplaySlot(i);
            }
        }

        return field_178823_g;
    }

    public void func_181140_a(final Entity p_181140_1_) {
        if (p_181140_1_ != null && !(p_181140_1_ instanceof EntityPlayer) && !p_181140_1_.isEntityAlive()) {
            final String s = p_181140_1_.getUniqueID().toString();
            this.removeObjectiveFromEntity(s, null);
            this.removePlayerFromTeams(s);
        }
    }
}
