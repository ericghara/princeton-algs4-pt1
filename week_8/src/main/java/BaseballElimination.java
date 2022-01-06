import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BaseballElimination {
    private final Division division;
    private HashMap<Integer,ArrayList<String>> certOfElim;

    // create a baseball division from given filename in format specified below
    public BaseballElimination(String filename) {
        division = new Division(filename);
    }
    // number of teams
    public int numberOfTeams() {
        return division.getNumberOfTeams();
    }
    // all teams
    public Iterable<String> teams() {
        return division.getTeamNames();
    }
    // number of wins for given team
    public int wins(String team) {
        Team t = division.getTeam(team);
        return t.getWins();
    }
    // number of losses for given team
    public int losses(String team) {
        Team t = division.getTeam(team);
        return t.getLosses();
    }
    // number of remaining games for given team
    public int remaining(String team) {
        Team t = division.getTeam(team);
        return t.getRemaining();
    }
    // number of remaining games between team1 and team2
    public int against(String team1, String team2) {
        Team t1 = division.getTeam(team1);
        Team t2 = division.getTeam(team2);
        return t1.getRemainingAgainst(t2);
    }

    // is given team eliminated?
    public boolean isEliminated(String team) {
        Team t = division.getTeam(team);
        eliminate(t, division);
        return t.isEliminated();
    }              

    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        Team t = division.getTeam(team);
        eliminate(t, division);
        return t.isEliminated() ? t.getCertOfElim() : null;
    }

    private static void eliminate(Team elimTeam, Division division) {
        if (elimTeam.hasCertOfElim()) { return; } // team already has COE calculated
        DivisionEdgeContainer edges = new DivisionEdgeContainer(elimTeam, division);
        int V = edges.getNumberOfVertices();
        FlowNetwork FN = new FlowNetwork(V);
        edges.forEach(FN::addEdge);
        FordFulkerson FF = new FordFulkerson(FN, DivisionEdgeContainer.Vertex.S.Id, DivisionEdgeContainer.Vertex.T.Id);
        List<String> COE = generateCertificateOfElimination(FF,edges,division);
        elimTeam.setCertOfElim(COE);
    }

    private static List<String> generateCertificateOfElimination(FordFulkerson FF, DivisionEdgeContainer edges, Division division) {
        ArrayList<String> COE = new ArrayList<>();
        int numTeams = edges.getNumberOfTeams();
        for (int teamID = 0; teamID < numTeams; teamID++) {
            int vertexID = edges.getVertexID(teamID);
            if (FF.inCut(vertexID)) {
                String name = division.getTeam(teamID)
                                      .getTeamName();
                COE.add(name);
            }
        }
        return COE;
    }

    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            }
            else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }

}
