import edu.princeton.cs.algs4.StdOut;

public class BaseballElimination {

    private final Division division;

    /**
     * Create a new baseball division from the filename specified.
     * @param filename input csv file with format specified above
     */
    public BaseballElimination(String filename) {
        division = new Division(filename);
    }

    /**
     * Number of teams in the division
     * @return number of teams in the division
     */
    public int numberOfTeams() {
        return division.getNumberOfTeams();
    }

    /**
     * All team names
     * @return team names as Strings
     */
    public Iterable<String> teams() {
        return division.getTeamNames();
    }

    /**
     * Total wins for a team so far in the season.
     * @param team team name, must exactly match input csv
     * @return number of wins
     */
    public int wins(String team) {
        Division.Team t = division.getTeam(team);
        return t.getWins();
    }

    /**
     * Total losses for a team so far in the season.
     * @param team name, must exactly match input csv
     * @return number of losses
     */
    public int losses(String team) {
        Division.Team t = division.getTeam(team);
        return t.getLosses();
    }

    /**
     * Total remaining games for a team in the season.
     * @param team name, must exactly match input csv
     * @return games remaining in the season
     */
    public int remaining(String team) {
        Division.Team t = division.getTeam(team);
        return t.getRemaining();
    }

    /**
     * Total remaining games between two teams in the division.
     * @param team1 a team name, must exactly match input csv
     * @param team2 a team name, must exactly match input csv
     * @return games remaining in the season
     */
    public int against(String team1, String team2) {
        Division.Team t1 = division.getTeam(team1);
        Division.Team t2 = division.getTeam(team2);
        return t1.getRemainingAgainst(t2);
    }

    /**
     * Check if a team has been eliminated from potentially winning (or tying for first place) the division.
     * @param team a team name, must exactly match input csv
     * @return true if eliminated, false if not eliminated
     */
    public boolean isEliminated(String team) {
        Division.Team t = division.getTeam(team);
        return t.isEliminated();
    }

    /**
     * The teams which eliminate the input team.  If a single team is returned this represents simple math that
     * another team has more wins the input team could possibly win.  If multiple teams are returned then a more
     * complex scenario is represented where any one team could lose all its games, but because of these losses
     * their competitors must win games.  Within this network all possible combinations of wins and losses would
     * exclude the input team.
     *
     * @param team a team name, must exactly match input csv
     * @return names of team(s) which eliminate the input team.
     */
    public Iterable<String> certificateOfElimination(String team) {
        Division.Team t = division.getTeam(team);
        return t.isEliminated() ? t.getCertOfElim() : null;
    }

    /**
     * A demo method which takes a league csv file as an input (see {@link BaseballElimination} for format) and
     * prints the teams which are eliminated along with their certificate of elimination.
     *
     * @param args path to league csv file
     */
    public static void main(String[] args) {
        BaseballElimination BE = new BaseballElimination(args[0]);
        for (String team : BE.teams()) {
            if (BE.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : BE.certificateOfElimination(team)) {
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
