package com.teamtreehouse.challenges.highscores;

import com.teamtreehouse.ast.Inspector;
import com.teamtreehouse.ast.predicates.Expressions;
import com.teamtreehouse.ast.rules.Sourcery;
import com.teamtreehouse.challenges.highscores.model.Score;
import com.teamtreehouse.teachingassistant.rules.Consoler;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assert.assertTrue;


public class MainTest {
  public static final String METHOD_NAME = "printBurgerTimeScoresDeclaratively";
  private Inspector inspector;

  @ClassRule
  public static Sourcery sourcery = new Sourcery();

  @Rule
  public Consoler consoler = new Consoler();
  private List<Score> filterScores;
  private List<Score> collectScores;


  @Before
  public void setUp() throws Exception {
    inspector = sourcery.inspectorFor(Main.class);
    filterScores = Arrays.asList(
            new Score("Burger Time", "", "", "1", "NOT_ENOUGH_POINTS"),
            new Score("Burger Time", "", "", "20,001", "VALID_1"),
            new Score("Dig Dug", "", "", "20,001", "WRONG_NAME")
    );
    collectScores = Arrays.asList(
            new Score("", "", "Nintendo Entertainment System", "", "FIRST"),
            new Score("", "", "Nintendo Entertainment System", "", "SECOND"),
            new Score("", "", "Sega Genesis", "", "WRONG"),
            new Score("", "", "Nintendo Entertainment System", "", "THIRD"),
            new Score("", "", "Nintendo Entertainment System", "", "TOO_MANY")

    );
  }

  @Test
  public void limitsCollectedResults() throws Exception {
    List<Score> actualScores = Main.getFirstThreeNintendoScoresDeclaratively(collectScores);

    assertThat("Ensure you are limiting the stream", actualScores.size(), is(3));
  }

  @Test
  public void collectedOnlyNESGames() throws Exception {
    List<Score> actualScores = Main.getFirstThreeNintendoScoresDeclaratively(collectScores);

    for (Score score : actualScores) {
      assertThat("Ensure you are only gathering 'Nintendo Entertainment System' scores",
              score.getPlatform(),
              is("Nintendo Entertainment System"));

    }
  }

  @Test
  public void collectedTheFirstThree() throws Exception {
    List<Score> actualScores = Main.getFirstThreeNintendoScoresDeclaratively(collectScores);

    List<String> players = actualScores.stream()
            .map(Score::getPlayer)
            .collect(Collectors.toList());
    assertThat("Make sure you are returning the correct scores", players, allOf(
            hasItems("FIRST", "SECOND", "THIRD"),
            not(hasItem("TOO_MANY"))
    ));
  }

  @Test
  public void streamIsUsed() throws Exception {
    String msg = String.format("Ensure you open a stream off of the scores collection in the '%s' method",
            METHOD_NAME);
    assertTrue(msg, inspector.matchingChain(chain -> chain
            .withExpression(Expressions.isMethodCall().and(Expressions.containing("stream")))
    ));
  }

  @Test
  public void filterIsCalled() throws Exception {
    String msg = String.format("Make sure you use the filter method off of your stream in the method '%s'",
            METHOD_NAME);
    assertTrue(msg, inspector.matchingChain(chain -> chain
      .withExpression(Expressions.isMethodCall()
              .and(Expressions.containing("filter")))
    ));
  }

  @Test
  public void appropriateBurgerTimeAppears() throws Exception {
    consoler.addExpectedPrompt("VALID_1");

    Main.printBurgerTimeScoresDeclaratively(filterScores);

    consoler.assertExpected("Expected to see Burger Time entry with 'VALID_1' for a name");
  }

  @Test
  public void onlyHighScores() throws Exception {
    Main.printBurgerTimeScoresDeclaratively(filterScores);

    consoler.assertNotPresent(
            "Make sure you display only scores with an amount greater or equal to 20,000",
            "NOT_ENOUGH_POINTS");
  }

  @Test
  public void onlyBurgerTime() throws Exception {
    Main.printBurgerTimeScoresDeclaratively(filterScores);

    consoler.assertNotPresent(
            "Make sure you display only scores with games named 'Burger Time'",
            "Dig Dug");

  }
}