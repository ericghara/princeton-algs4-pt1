import edu.princeton.cs.algs4.FlowEdge;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.stream.Stream;


public class DivisionEdgeContainer implements Iterable<FlowEdge>{
    public enum Vertex {
        S(0), T(1); // source and sink respectively
        final int ID;

        Vertex(int ID) {
            this.ID = ID;
        }
    }
    static int TEAMID_OFFSET = Vertex.values().length; // team vertices after enums

    int numTeams;
    FlowEdge[] teamToSink;
    LinkedList<FlowEdge> sourceToGame;
    LinkedList<FlowEdge> gameToTeam;

    public DivisionEdgeContainer(Team elimTeam, Division division) {
        numTeams = division.getNumberOfTeams();
        teamToSink = new FlowEdge[this.numTeams];
        sourceToGame = new LinkedList<>();
        gameToTeam = new LinkedList<>();
        addTeams(elimTeam, division);
        addGames(elimTeam, division);
    }

    private void addGames(Team elimTeam, Division division) {

        // looping in a way to avoid duplicate games, ie add team 2 vs team 1 but not duplicate team 1 vs team 2
        // start from 1 because team 0 will always just return games against itself (ie 0)
        int elimTeamID = elimTeam.getId();
        for (int opp1ID = 1; opp1ID < numTeams; opp1ID++) {
            if (opp1ID == elimTeamID) {
                continue;
            }
            Iterator<Integer> gameSchedule = division.getTeam(opp1ID)
                    .getGameSchedule()
                    .iterator();
            int opp2ID = 0;
            for (int opp2Id = 0; opp2ID < opp1ID; opp2ID++) {
                int numGames = gameSchedule.next();
                if (numGames > 0 && opp2ID != elimTeamID) {
                    addGamesVertex(opp1ID, opp2ID, numGames);
                }
                opp2ID++;
            }
        }
    }


    private void addTeams(Team elimTeam, Division division) {
        int N = division.getNumberOfTeams();
        int maxAllowedWins = elimTeam.getMaxDivisionWins();
        for (int teamNum = 0; teamNum < N; teamNum++) {
            Team team = division.getTeam(teamNum);
            // note initializes edge for elimTeam, but since no other edges will connect to it, vertex will always be on
            // sink side of min cut.
            int winCapacity = maxAllowedWins - team.getWins();
            addTeam(teamNum, winCapacity);
        }
    }

    public void addGamesVertex(int teamID1, int teamID2, int numGames) {
        int gameVertID = getNextGameID();
        int teamVertID1 = getVertexID(teamID1);
        int teamVertID2 = getVertexID(teamID2);

        addSourceToGame(gameVertID, numGames);
        addGameToTeam(gameVertID, teamVertID1);
        addGameToTeam(gameVertID, teamVertID2);
    }

    public void addTeam(int teamID, int maxWins) {
        int teamVertID = getVertexID(teamID);
        teamToSink[teamVertID] = new FlowEdge(teamVertID, Vertex.T.ID, maxWins);
    }

    private void addSourceToGame(int gameVertID, int numGames) {
        FlowEdge edge = new FlowEdge(Vertex.S.ID, gameVertID, numGames);
        sourceToGame.add(edge);
    }

    private void addGameToTeam(int gameVertID, int teamVertID) {
        FlowEdge edge = new FlowEdge(gameVertID, teamVertID, Double.POSITIVE_INFINITY);
        gameToTeam.add(edge);
    }

    public int getNextGameID() {
        return getNumberOfVertices();
    }

    public int getVertexID(int teamID) {
        verifyTeamID(teamID);
        return  TEAMID_OFFSET + teamID;
    }

    private void verifyTeamID(int teamID) {
        if (teamID >= numTeams || teamID < 0) {
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
