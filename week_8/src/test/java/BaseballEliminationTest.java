import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class BaseballEliminationTest {

    static String TEST_LEAGUE = "src/test/resources/teams4.txt";

    /**
     * Opens and reads a CSV file specified by the {@code filepath}.  The {@code delimPattern}
     * should be a valid {@link java.util.regex.Pattern}.
     *
     * @param filepath     path to csv file
     * @param delimPattern a valid {@link java.util.regex.Pattern}.
     * @return a (potentially irregular) {@code String} matrix.
     */
    static String[][] readCsv(String filepath, String delimPattern) {
        Scanner scanner;
        try {
            File file = new File(filepath);
            scanner = new Scanner(file);
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not open the specified file");
        }
        ArrayList<String[]> parsedFile = new ArrayList<>();
        Pattern pattern = Pattern.compile(delimPattern);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] row = pattern.split(line);
            parsedFile.add(row);
        }
        return parsedFile.toArray(new String[0][0]);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/isEliminatedTest.csv", delimiterString = ", ")
    void isEliminated(String testFile, String elimTeams) {
        BaseballElimination BE = new BaseballElimination(testFile);
        String[] expected = elimTeams.split("\\W+");
        Arrays.asList(expected).forEach((name) -> assertTrue(BE.isEliminated(name)));
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/certificateOfEliminationTest.csv", delimiterString = ", ")
    void certificateOfElimination(String testFile, String elimTeam, String CertOfElim) {
        BaseballElimination BE = new BaseballElimination(testFile);
        String[] expectedCOE = CertOfElim.split("\\W+");
        LinkedList<String> COE = new LinkedList<>();
        BE.certificateOfElimination(elimTeam)
            .forEach(COE::add);
        String[] actualCOE = COE.toArray(new String[0]);
        Arrays.sort(expectedCOE);
        Arrays.sort(actualCOE);
        Assertions.assertArrayEquals(expectedCOE, actualCOE);
    }


    @TestInstance(Lifecycle.PER_CLASS)
    @Nested
    class simpleDataStrucureTests {
        final int HEADER = 1; // first data row, num teams at row 0
        final int TEAMNAME_COL = 0;
        final int WINS_COL = 1;
        final int LOSSES_COL = 2;
        final int REMAIN_COL = 3;
        final int FIRST_LEAGUE_COL = 4; // league games listed here to the end

        private final String[][] leagueMatrix = readCsv(TEST_LEAGUE, "\\W+");
        BaseballElimination BE = new BaseballElimination(TEST_LEAGUE);


        <T> Iterable<T> getColumn(int colNum, Function<String, T> typeConverter) {
            LinkedList<T> column = new LinkedList<>();
            for (int i = 0; i < BE.numberOfTeams(); i++) {
                int r = i + HEADER;
                T convertedData = typeConverter.apply(leagueMatrix[r][colNum]);
                column.add(convertedData);
            }
            return column;
        }

        Stream<Arguments> teamsTestSource() {
            Iterator<String> expected = getColumn(TEAMNAME_COL, String::toString).iterator();
            Iterator<String> actual = BE.teams().iterator();
            LinkedList<Arguments> args = new LinkedList<>();
            args.add(Arguments.arguments(expected, actual));
            return args.stream();
        }

        /**
         * Tests that two iterators are equal.
         *
         * @param expected test iterator
         * @param actual   correct iterator
         * @param <T>      type of iterators to be compared
         */

        @MethodSource("teamsTestSource")
        @ParameterizedTest(name = "teamsTest {index}")
        <T> void iteratorTester(Iterator<T> expected, Iterator<T> actual) {
            while (expected.hasNext()) {
                Assertions.assertTrue(actual.hasNext());
                Assertions.assertEquals(expected.next(), actual.next());
            }
            Assertions.assertFalse(actual.hasNext());
        }


        @Test
        void numberOfTeamsTest() {
            Assertions.assertEquals(Integer.parseInt(leagueMatrix[0][0]), BE.numberOfTeams());
        }

        @Test
        void winsTest() {
            Iterable<String> teamNames = getColumn(TEAMNAME_COL,String::toString);
            Iterator<Integer> wins = getColumn(WINS_COL,Integer::parseInt).iterator();
            teamNames.forEach((n) -> Assertions.assertEquals(wins.next(),BE.wins(n)));
        }

        @Test
        void lossesTest() {
            Iterable<String> teamNames = getColumn(TEAMNAME_COL,String::toString);
            Iterator<Integer> loss = getColumn(LOSSES_COL,Integer::parseInt).iterator();
            teamNames.forEach((n) -> Assertions.assertEquals(loss.next(),BE.losses(n)));
        }

        @Test
        void remainingTest() {
            Iterable<String> teamNames = getColumn(TEAMNAME_COL,String::toString);
            Iterator<Integer> remain = getColumn(REMAIN_COL,Integer::parseInt).iterator();
            teamNames.forEach((n) -> Assertions.assertEquals(remain.next(),BE.remaining(n)));
        }

        @Test
        void againstTest() {
            int N = BE.numberOfTeams();
            ArrayList<String> teamNames = new ArrayList<>(N);
            getColumn(TEAMNAME_COL,String::toString).forEach(teamNames::add);
            for (int j = 0; j < N; j++) {
                int col = j + FIRST_LEAGUE_COL;
                Iterator<Integer> actuals = getColumn(col,Integer::parseInt).iterator();
                for (int i = 0; i < N; i++) {
                    int actual = BE.against(teamNames.get(i), teamNames.get(j));
                    Assertions.assertEquals(actuals.next(),actual);
                }
            }
        }
    }
}