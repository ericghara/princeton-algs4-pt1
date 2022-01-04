import edu.princeton.cs.algs4.In;

import java.util.HashMap;
import java.util.stream.IntStream;

public class League {
    private final int N; // number of teams
    private final int[][] gameSchedule;
    private final int[] wins, losses, remaining;
    private final String[] teamNames;
    private final HashMap<String,Integer> teamNumber;
    private final boolean[] triviallyEliminated; // impossible to win more games than another team already has, flow analysis not required

    public League(String filePath) {
        In file = openFile(filePath);
        N = file.readInt();

        wins = new int[N];
        losses = new int[N];
        remaining = new int[N];
        teamNames = new String[N];
        triviallyEliminated = new boolean[N];
        gameSchedule = new int[N][N];
        teamNumber = new HashMap<>();

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
        return teamNumber.keySet();
    }

    public int getWins(int teamNum) {
        return wins[teamNum];
    }

    public int getLosses(int teamNum) {
        return losses[teamNum];
    }

    public int getRemaining(int teamNum) {
        return remaining[teamNum];
    }

    public int getRemainingAgainst(int teamNum1, int teamNum2) {
        return gameSchedule[teamNum1][teamNum2];
    }

    public boolean isTriviallyEliminated(int teamNum) {
        return triviallyEliminated[teamNum];
    }

    public int getTeamNum(String teamName) {
        int teamNum = teamNumber.getOrDefault(teamName, -1);
        if (teamNum < 0) {
            throw new IllegalArgumentException("Received an invalid team name.");
        }
        return teamNum;
    }

    private void addTeam(int teamNum, In file) {
        String name = file.readString();
        teamNames[teamNum] = name;
        teamNumber.put(name, teamNum);

        wins[teamNum] = file.readInt();
        losses[teamNum] = file.readInt();
        remaining[teamNum] = file.readInt();

        for (int i = 0; i < N; i++) {
            gameSchedule[teamNum][i] = file.readInt();
        }
    }

    private static In openFile(String filePath) {
        return new In(filePath);
    }

    private static void closeFile(In file) {
        file.close();
    }

    private void triviallyEliminate() {
        int maxW = IntStream.of(wins)
                            .max()
                            .orElseThrow( () -> new IllegalArgumentException("Received an empty wins array"));
        for (int teamNum = 0; teamNum < N; teamNum++) {
            int w = wins[teamNum];
            int r = remaining[teamNum];
            if ( w + r < maxW ) {
                triviallyEliminated[teamNum] = true;
            }
        }
    }




}
