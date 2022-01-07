import edu.princeton.cs.algs4.FlowEdge;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.stream.Stream;


public class DivisionEdgeContainer implements Iterable<FlowEdge>{
    public enum Vertex {
        S(0), T(1); // source and sink respectively
        final int Id;

        Vertex(int Id) {
            this.Id = Id;
        }
    }
    static int TEAMID_OFFSET = Vertex.values().length; // team vertices enumerated after enums

    private final int numTeams;
    private final FlowEdge[] teamToSink;
    private final LinkedList<FlowEdge> sourceToGame;
    private final LinkedList<FlowEdge> gameToTeam;

    public DivisionEdgeContainer(Team elimTeam, Division division) {
        numTeams = division.getNumberOfTeams();
        teamToSink = new FlowEdge[this.numTeams];
        sourceToGame = new LinkedList<>();
        gameToTeam = new LinkedList<>();
        addTeams(elimTeam, division);
        addGames(elimTeam, division);
    }

    private void addGames(Team elimTeam, Division division) {
        int elimTeamId = elimTeam.getId();
        for (Team opp1 : division) {
            int opp1_ID = opp1.getId();
            if (opp1_ID == elimTeamId) {
                continue;
            }
            for (Team opp2 : division) {
                int opp2_ID = opp2.getId();
                // Avoids double counting games ie only adds 2 vs 1 instead of both 2 vs 1 and 1 vs 2.
                if (opp2_ID >= opp1_ID) { break; }
                int numGames = opp1.getRemainingAgainst(opp2);
                // games against elimTeam are assumed to be a loss for opp1, so are not counted
                if (numGames > 0 && opp2_ID != elimTeamId) {
                    addGamesVertex(opp1_ID, opp2_ID, numGames);
                }
            }
        }
    }


    private void addTeams(Team elimTeam, Division division) {
        int maxAllowedWins = elimTeam.getMaxDivisionWins();
        for (Team team : division) {
            // note initializes edge for elimTeam, but since no other edges will connect to it, vertex will always be on
            // sink side of min cut.
            int winCapacity = maxAllowedWins - team.getWins();
            addTeam(team.getId(), winCapacity);
        }
    }

    public void addGamesVertex(int teamId1, int teamId2, int numGames) {
        int gameVertID = getNextGameId();
        int teamVertID1 = getVertexID(teamId1);
        int teamVertID2 = getVertexID(teamId2);

        addSourceToGame(gameVertID, numGames);
        addGameToTeam(gameVertID, teamVertID1);
        addGameToTeam(gameVertID, teamVertID2);
    }

    public void addTeam(int teamId, int maxWins) {
        int teamVertId = getVertexID(teamId);
        teamToSink[teamId] = new FlowEdge(teamVertId, Vertex.T.Id, maxWins);
    }

    private void addSourceToGame(int gameVertID, int numGames) {
        FlowEdge edge = new FlowEdge(Vertex.S.Id, gameVertID, numGames);
        sourceToGame.add(edge);
    }

    private void addGameToTeam(int gameVertID, int teamVertID) {
        FlowEdge edge = new FlowEdge(gameVertID, teamVertID, Double.POSITIVE_INFINITY);
        gameToTeam.add(edge);
    }

    public int getNextGameId() {
        return getNumberOfVertices();
    }

    public int getVertexID(int teamId) {
        verifyTeamId(teamId);
        return  TEAMID_OFFSET + teamId;
    }

    private void verifyTeamId(int teamId) {
        if (teamId >= numTeams || teamId < 0) {
            throw new IllegalArgumentException("vertexID does not match a valid team");
        }
    }

    public int getNumberOfVertices() {
        int numGames = sourceToGame.size();
        return TEAMID_OFFSET + numTeams + numGames;
    }

    public int getNumberOfTeams() {
        return numTeams;
    }

    @Override
    public Iterator<FlowEdge> iterator() {
        Stream.Builder<FlowEdge> allEdges = Stream.builder();
        sourceToGame.forEach(allEdges);
        gameToTeam.forEach(allEdges);
        Arrays.asList(teamToSink)
              .forEach(allEdges);
        return allEdges.build()
                       .iterator();
    }
}
