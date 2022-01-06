import java.util.Spliterator;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Team implements Comparable<Team> {
    private final int id, wins, losses, remaining;
    private String teamName;
    private final int[] gameSchedule;
    private Boolean isEliminated;
    private List<String> certOfElim;

    public Team(String teamName, int id, int wins, int losses, int remaining, int[] gameSchedule) {
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
     * Returns: <ul><li>true if eliminated</li><li>false if not eliminated</li><li>null if elimination status
     * hasn't been calculated</li></ul>
     * @return optional Boolean
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

    public void setCertOfElim(List<String> certOfElim) {
        this.certOfElim = certOfElim;
        isEliminated = !certOfElim.isEmpty();
    }

    @Override
    public int compareTo(Team that) {
        return Integer.compare(this.wins,that.getWins());
    }
}
