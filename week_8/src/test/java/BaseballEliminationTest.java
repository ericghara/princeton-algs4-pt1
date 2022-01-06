import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

class BaseballEliminationTest {

    static String RESOURCES_DIR = "src/test/resources/";

    @ParameterizedTest
    @CsvFileSource(resources = "/test_outline.csv", delimiter = '|')
    void isEliminated(String testFile, String elimTeams) {
        BaseballElimination BE = new BaseballElimination(testFile);
        String[] expected = elimTeams.split(" ");
        Arrays.asList(expected).forEach((name) -> assertTrue(BE.isEliminated(name)));
    }

    @ParameterizedTest
    @ValueSource(strings = "src/test/resources/teams4.txt")
    void certificateOfElimination(String testFile) {
        BaseballElimination BE = new BaseballElimination(testFile);
        LinkedList<String> COE = new LinkedList<>();
        BE.certificateOfElimination("Philadelphia")
          .forEach(COE::add);
        assertTrue(COE.size() >= 2);
    }
}