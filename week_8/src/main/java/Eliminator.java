import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

class Eliminator {

    enum Vertex {
        SOURCE(0), SINK(1); // source and sink respectively
        final int Id;

        Vertex(int Id) {
            this.Id = Id;
        }
    }

    private final int TEAMID_OFFSET = Vertex.values().length; // team vertices enumerated after enums

    private final int numTeams;
    private final FlowEdge[] teamToSink;
    private final LinkedList<FlowEdge> sourceToGame;
    private final LinkedList<FlowEdge> gameToTeam;
    private final Division division;
    private final Division.Team elimTeam;

    /**
     * Determines elimination status of teams within a division.
     * @see Division.Team#setCertOfElim
     * @param division a division of teams
     */
    static void processDivision(Division division) {
        Division.Team maxW = StreamSupport.stream(division.spliterator(), false)
                .max(Comparator.comparingInt(Division.Team::getWins))
                .orElseThrow( () -> new IllegalArgumentException("Received an empty teams array"));
        String[] certOfElim = {maxW.getTeamName()};

        for (Division.Team t:division) {
            /*Perform "trivial" elimination: t does not have enough remaining games in the season to
             * win as many games as another team already has */
            if ( t.getMaxPossibleWins() < maxW.getWins() ) {
                t.setCertOfElim(Arrays.asList(certOfElim));
            }
            /*Perform a flow analysis to determine if more complex elimination scenario exists*/
            else {
                Eliminator eliminator = new Eliminator(t, division);
                eliminator.eliminate();
            }
        }
    }

    /**
     * Creates an eliminator instance for a single team in the division.
     *
     * @param elimTeam team to perform elimination modeling on
     * @param division division which the team is a member of
     */
    private Eliminator(Division.Team elimTeam, Division division) {
        this.division = division;
        this.elimTeam = elimTeam;
        numTeams = division.getNumberOfTeams();
        teamToSink = new FlowEdge[this.numTeams];
        sourceToGame = new LinkedList<>();
        gameToTeam = new LinkedList<>();
        addTeams();
        addGames();
    }

    /**
     * Adds all edges which connect to game vertices.  For each game vertex 3 edges are created:
     * <ol>
     *     <li>source to vertex representing game between team 1 and team 2</li>
     *     <li>game vertex to team 1 vertex</li>
     *     <li>game vertex to team 2 vertex</li>
     * </ol>
     */
    private void addGames() {
        int elimTeamId = elimTeam.getId();
        for (Division.Team opp1 : division) {
            int opp1_ID = opp1.getId();
            if (opp1_ID == elimTeamId) {
                continue;
            }
            for (Division.Team opp2 : division) {
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

    /**
     * Add edges which connect team vertices to the sink.
     */
    private void addTeams() {
        int maxAllowedWins = elimTeam.getMaxPossibleWins();
        for (Division.Team team : division) {
            // note initializes edge for elimTeam, but since no other edges will connect to it, vertex will always be on
            // sink side of min cut.
            int winCapacity = maxAllowedWins - team.getWins();
            addTeam(team.getId(), winCapacity);
        }
    }

    /**
     * Adds edges for the remaining games between team1 and team2.  The number of games represents the flow
     * capacity of the source to the game vertex.  The edges connecting the game vertex to the respective team
     * verticies have infinite capacity.
     *
     * @param teamId1 ID of one team in the game
     * @param teamId2 ID of the other team in the game
     * @param numGames number of games left between teams in the season
     * @see Eliminator#addGames()
     */
    private void addGamesVertex(int teamId1, int teamId2, int numGames) {
        int gameVertID = getNextGameId();
        int teamVertID1 = getVertexID(teamId1);
        int teamVertID2 = getVertexID(teamId2);

        addSourceToGame(gameVertID, numGames);
        addGameToTeam(gameVertID, teamVertID1);
        addGameToTeam(gameVertID, teamVertID2);
    }

    /**
     * Adds the team to sink vertex for a single team.  The capacity of this vertex represents the number of games
     * that this team may win before the {@code elimTeam} would be eliminated.  To ensure this value is not negative
     * a trivial elemination check must be performed before calling.
     *
     * @param teamId id of team to add
     * @param maxWins number of games team may win before knocking out the {@code elimTeam} from the playoffs
     * @see Eliminator#addTeams()
     * @see Eliminator#processDivision(Division)
     */
    private void addTeam(int teamId, int maxWins) {
        int teamVertId = getVertexID(teamId);
        teamToSink[teamId] = new FlowEdge(teamVertId, Vertex.SINK.Id, maxWins);
    }

    private void addSourceToGame(int gameVertID, int numGames) {
        FlowEdge edge = new FlowEdge(Vertex.SOURCE.Id, gameVertID, numGames);
        sourceToGame.add(edge);
    }

    private void addGameToTeam(int gameVertID, int teamVertID) {
        FlowEdge edge = new FlowEdge(gameVertID, teamVertID, Double.POSITIVE_INFINITY);
        gameToTeam.add(edge);
    }

    private int getNextGameId() {
        return getNumberOfVertices();
    }

    private void verifyTeamId(int teamId) {
        if (teamId >= numTeams || teamId < 0) {
            throw new IllegalArgumentException("vertexID does not match a valid team");
        }
    }

    /**
     * Returns the vertex ID (unique number) for the specified team ID
     *
     * @see Division.Team#getId()
     * @param teamId the team's ID
     * @return a vertex ID
     */
    private int getVertexID(int teamId) {
        verifyTeamId(teamId);
        return  TEAMID_OFFSET + teamId;
    }

    /**
     * Number of vertices referenced by the {@link edu.princeton.cs.algs4.FlowEdge}s in this instance.
     *
     * @return the number of vertices
     */
    private int getNumberOfVertices() {
        int numGames = sourceToGame.size();
        return TEAMID_OFFSET + numTeams + numGames;
    }

    /**
     * Provides all {@link edu.princeton.cs.algs4.FlowEdge}s required to model the division in a {@link edu.princeton.cs.algs4.FordFulkerson}
     * flow analysis.
     *
     * @return all {@link edu.princeton.cs.algs4.FlowEdge}s required to model the division
     */

    private Stream<FlowEdge> getAllEdges() {
        Stream.Builder<FlowEdge> allEdges = Stream.builder();
        sourceToGame.forEach(allEdges);
        gameToTeam.forEach(allEdges);
        Arrays.asList(teamToSink)
                .forEach(allEdges);
        return allEdges.build();
    }

    /**
     * Calculates then sets the elimination status of a team and sets the certificate of elimination.
     *
     * @see Division.Team#setCertOfElim(List)
     */
    private void eliminate() {
        if (elimTeam.hasCertOfElim()) { return; } // team already has COE calculated
        int V = getNumberOfVertices();
        FlowNetwork FN = new FlowNetwork(V);
        getAllEdges().forEach(FN::addEdge);
        FordFulkerson FF = new FordFulkerson(FN, Vertex.SOURCE.Id, Vertex.SINK.Id);
        List<String> COE = generateCertificateOfElimination(FF);
        elimTeam.setCertOfElim(COE);
    }

    /**
     * Generates a Certificate of Elimination as a list of team name Strings
     *
     * @see BaseballElimination#certificateOfElimination(String)
     * @param FF a {@link edu.princeton.cs.algs4.FordFulkerson} flow network representation of the division
     * @return a certificate of elimination containing the team name(s) which eliminate the subject team of the {@code FF} network
     */
    private List<String> generateCertificateOfElimination(FordFulkerson FF) {
        ArrayList<String> COE = new ArrayList<>();
        int numTeams = division.getNumberOfTeams();
        for (int teamID = 0; teamID < numTeams; teamID++) {
            int vertexID = getVertexID(teamID);
            if (FF.inCut(vertexID)) {
                String name = division.getTeam(teamID)
                        .getTeamName();
                COE.add(name);
            }
        }
        return COE;
    }
}