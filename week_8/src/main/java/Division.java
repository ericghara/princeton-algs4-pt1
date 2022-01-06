import edu.princeton.cs.algs4.In;

import java.util.Objects;
import java.util.Iterator;
import java.util.Comparator;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Division implements Iterable<Team> {
    private final int N; // number of teams
    private final Team[] teams;
    private final LinkedHashMap<String,Team> teamLookup; // Linked for easier testing of getTeamNames();

    public Division(String filePath) {
        In file = openFile(filePath);
        N = file.readInt();
        teams = new Team[N];
        teamLookup = new LinkedHashMap<>();

        for (int teamNum = 0; teamNum < N; teamNum++) {
            addTeam(teamNum, file);
        }

        closeFile(file);
        triviallyEliminate();
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

    private static In openFile(String filePath) {
        return new In(filePath);
    }

    private static void closeFile(In file) {
        file.close();
    }

    /**
     * Performs simple preprocessing to eliminate teams based on the fact that one team has more wins than
     * another team could ever possibly win with remaining games (irrespective of who they are against).  This
     * is an optimization that prevents doing rigorous analysis for simple cases.
     */
    private void triviallyEliminate() {
        Team maxW = Stream.of(teams)
                            .max(Comparator.naturalOrder())
                            .orElseThrow( () -> new IllegalArgumentException("Received an empty teams array"));
        String[] certOfElim = {maxW.getTeamName()};

        for (Team t:teams) {
            if ( t.getMaxDivisionWins() < maxW.getWins() ) {
                t.setCertOfElim(Arrays.asList(certOfElim));
            }
        }
    }

    @Override
    public Iterator<Team> iterator() {
        return Arrays.asList(teams)
                     .iterator();
    }
}
