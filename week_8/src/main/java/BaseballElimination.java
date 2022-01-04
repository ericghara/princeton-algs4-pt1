import edu.princeton.cs.algs4.StdOut;

public class BaseballElimination {
    private final League league;

    // create a baseball division from given filename in format specified below
    public BaseballElimination(String filename) {
        league = new League(filename);

    }
    // number of teams
    public int numberOfTeams() {
        return league.getNumberOfTeams();
    }
    // all teams
    public Iterable<String> teams() {
        return league.getTeamNames();
    }
    // number of wins for given team
    public int wins(String team) {
        int teamNum = league.getTeamNum(team);
        return league.getWins(teamNum);
    }
    // number of losses for given team
    public int losses(String team) {
        int teamNum = league.getTeamNum(team);
        return league.getLosses(teamNum);
    }
    // number of remaining games for given team
    public int remaining(String team) {
        int teamNum = league.getTeamNum(team);
        return league.getRemaining(teamNum);
    }
    // number of remaining games between team1 and team2
    public int against(String team1, String team2) {
        int teamNum1 = league.getTeamNum(team1);
        int teamNum2 = league.getTeamNum(team2);
        return league.getRemainingAgainst(teamNum1, teamNum2);
    }

    // is given team eliminated?
    public boolean isEliminated(String team) {

    }              

    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {

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
