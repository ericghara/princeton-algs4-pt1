import edu.princeton.cs.algs4.In;

import java.util.List;
import java.util.Objects;
import java.util.Iterator;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Spliterator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

/**
 * This class allows a set of teams to be represented as a division.  This class is mostly a data class, but it does
 * make one important modification to some of its member teams upon instantiation -- teams which can be mathematically
 * eliminated from winning the division are eliminated.  For all other teams the {@link Team#isEliminated()} status
 * remains undefined.
 *
 * @see Team#setCertOfElim(List)
 *
 */
public class Division implements Iterable<Division.Team> {
    private final int N; // number of teams
    private final Team[] teams;
    private final LinkedHashMap<String,Team> teamLookup; // Linked so getTeamNames() has a predictable (testable) order

    /**
     * Reads in teams and their stats so far in the season from a csv file and creates a {@code division} instance.
     * Format: <ul><li>First line: number of teams</li>
     * <li>Each subsequent line represents one team</li><ul><li>first column - number of wins</li><li>second column - number
     * of losses</li><li>third column - total games remaining in season</li><li>remaining N (equals number of teams) columns -
     * games against team i</li></ul></ul>
     * @param filePath path to division csv file
     */
    public Division(String filePath) {
        In file = new In(filePath);
        N = file.readInt();
        teams = new Team[N];
        teamLookup = new LinkedHashMap<>();

        for (int teamNum = 0; teamNum < N; teamNum++) {
            addTeam(teamNum, file);
        }

        file.close();
        Eliminator.processDivision(this);
    }

    public int getNumberOfTeams() {
        return N;
    }

    public Iterable<String> getTeamNames() {
        return teamLookup.keySet();
    }

    public Team getTeam(int teamNum) {
        return teams[teamNum];
    }

    public Team getTeam(String teamName) {
        int teamNum = getTeamId(teamName);
        return getTeam(teamNum);
    }

    public int getTeamId(String teamName) {
        Team team = teamLookup.get(teamName);
        if (Objects.isNull(team)) {
            throw new IllegalArgumentException("Received an invalid team name.");
        }
        return team.getId();
    }

    private void addTeam(int teamId, In file) {
        String teamName = file.readString();
        int wins = file.readInt();
        int losses = file.readInt();
        int remaining = file.readInt();
        int[] gameSchedule = IntStream.generate(file::readInt)
                                      .limit(N)
                                      .toArray();
        Team team = new Team(teamName, teamId, wins, losses, remaining, gameSchedule);
        teams[teamId] = team;
        teamLookup.put(teamName, team);
    }

    @Override
    public Iterator<Team> iterator() {
        return Arrays.asList(teams)
                .iterator();
    }

    @Override
    public Spliterator<Team> spliterator() {
        return Arrays.asList(teams)
                .spliterator();
    }

    static class Team {
        private final int id, wins, losses, remaining;
        private final String teamName;
        private final int[] gameSchedule;
        private Boolean isEliminated;
        private List<String> certOfElim;

        Team(String teamName, int id, int wins, int losses, int remaining, int[] gameSchedule) {
            this.teamName = teamName;
            this.id = id;
            this.wins = wins;
            this.losses = losses;
            this.remaining = remaining;
            this.gameSchedule = gameSchedule;
            isEliminated = null;
        }

        public int getWins() {
            return wins;
        }

        /**
         * Max division wins are not equal to wins + remaining, as remaining includes games that are outside of the division
         * @return wins + Σ(division games remaining)
         */
        public int getMaxDivisionWins() {
            int w = getWins();
            Spliterator<Integer> games = getGameSchedule().spliterator();
            return StreamSupport.stream(games, false)
                                .reduce(w, Integer::sum);
        }

        public int getLosses() {
            return losses;
        }

        public int getRemaining() {
            return remaining;
        }

        /**
         * Returns the team's ID.  The ID is specified when the team is instantiated.
         *
         * @return the team's ID
         */
        public int getId() {
            return id;
        }

        public Iterable<Integer> getGameSchedule() {
            return Arrays.stream(gameSchedule)
                         .boxed()
                         .collect(Collectors.toList());
        }

        public int getRemainingAgainst(Team that) {
            return gameSchedule[that.getId()];
        }

        public String getTeamName() {
            return teamName;
        }

        public boolean hasCertOfElim() {
            return !Objects.isNull(isEliminated);
        }

        /**
         * Returns elimination status of this team. <ul><li>true if eliminated</li><li>false if not eliminated</li><li>null if elimination status
         * hasn't been set</li></ul>
         * @return optional Boolean
         * @see Team#setCertOfElim
         */
        public Boolean isEliminated() {
            if (!hasCertOfElim()) {
                throw new IllegalArgumentException("Improper usage Certificate of elimination has not been calculated -- consider calling hasCertOfElim() before isEliminated()");
            }
            return isEliminated;
        }

        public List<String> getCertOfElim() {
            return certOfElim;
        }

        /**
         * Updates elimination status and certificate of elimination for the Division.Team.
         *
         * @param certOfElim name(s) of team(s) which eliminate this team
         * @see Team#isEliminated()
         * @see Team#getCertOfElim()
         */
        public void setCertOfElim(List<String> certOfElim) {
            this.certOfElim = certOfElim;
            isEliminated = !certOfElim.isEmpty();
        }

        public int getMaxPossibleWins() {
            return wins + remaining;
        }
    }
}
