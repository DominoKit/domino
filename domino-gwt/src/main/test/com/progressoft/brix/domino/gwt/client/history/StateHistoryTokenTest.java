package com.progressoft.brix.domino.gwt.client.history;

import com.progressoft.brix.domino.api.shared.history.HistoryToken;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class StateHistoryTokenTest {

    private StateHistoryToken make(String token) {
        return new StateHistoryToken(token);
    }

    @Test(expected = HistoryToken.TokenCannotBeNullException.class)
    public void createWithNullToken_shouldThrowException() {
        make(null);
    }

    @Test
    public void createTokenWithSlashOnly_thenTokenValueShouldBeEmptyString() {
        assertThat(make("/").value()).isEmpty();
    }

    @Test
    public void createTokenWithSlashAndSinglePath_thenTokenValueShouldBeTheSinglePathString() {
        assertThat(make("/somePath").value()).isEqualTo("somePath");
    }

    @Test
    public void createTokenWithSlashAndMultiPaths_thenTokenValueShouldBeThePathsString() {
        assertThat(make("/firstPath/secondPath/thirdPath").value()).isEqualTo("firstPath/secondPath/thirdPath");
    }

    @Test
    public void createTokenWithMultiPaths_whenCheckingForStartingWithPathPassingFirstPath_thenShouldReturnTrue() {
        assertThat(make("firstPath/secondPath/thirdPath").startsWithPath("firstPath")).isTrue();
    }

    @Test
    public void createTokenWithMultiPaths_whenCheckingForStartingWithPathPassingWrongPath_thenShouldReturnFalse() {
        assertThat(make("firstPath/secondPath/thirdPath").startsWithPath("wrongPath")).isFalse();
    }

    @Test
    public void createTokenWithMultiPaths_whenCheckingForStartingWithPassingValidMultiPaths_thenShouldReturnTrue() {
        assertThat(make("firstPath/secondPath/thirdPath").startsWithPath("firstPath/secondPath")).isTrue();
    }

    @Test
    public void createTokenWithMultiPaths_whenCheckingForStartingWithPassingWrongMultiPaths_thenShouldReturnFalse() {
        assertThat(make("firstPath/secondPath/thirdPath").startsWithPath("firstPath/second")).isFalse();
    }

    @Test
    public void createTokenWithMultiPaths_whenCheckingForStartingWithPassingEmptyPath_thenShouldReturnFalse() {
        assertThat(make("firstPath/secondPath/thirdPath").startsWithPath("")).isFalse();
    }

    @Test
    public void createTokenWithMultiPaths_whenCheckingForStartingWithPassingNullPath_thenShouldReturnFalse() {
        assertThat(make("firstPath/secondPath/thirdPath").startsWithPath(null)).isFalse();
    }

    @Test
    public void createTokenWithMultiPaths_whenCheckingForStartingWithPassingLongerPath_thenShouldReturnFalse() {
        assertThat(make("firstPath/secondPath/thirdPath").startsWithPath("firstPath/secondPath/thirdPath/fourthPath"))
                .isFalse();
    }

    //-------------------------

    @Test
    public void createTokenWithMultiPaths_whenCheckingForEndsWithPathPassingFirstPath_thenShouldReturnTrue() {
        assertThat(make("firstPath/secondPath/thirdPath").endsWithPath("thirdPath")).isTrue();
    }

    @Test
    public void createTokenWithMultiPaths_whenCheckingForEndsWithPathPassingWrongPath_thenShouldReturnFalse() {
        assertThat(make("firstPath/secondPath/thirdPath").endsWithPath("wrongPath")).isFalse();
    }

    @Test
    public void createTokenWithMultiPaths_whenCheckingForEndWithPassingValidMultiPaths_thenShouldReturnTrue() {
        assertThat(make("firstPath/secondPath/thirdPath").endsWithPath("secondPath/thirdPath")).isTrue();
    }

    @Test
    public void createTokenWithMultiPaths_whenCheckingForEndsWithPassingWrongMultiPaths_thenShouldReturnFalse() {
        assertThat(make("firstPath/secondPath/thirdPath").endsWithPath("Path/thirdPath")).isFalse();
        assertThat(make("firstPath/secondPath/thirdPath").endsWithPath("secondPath/third")).isFalse();
    }

    @Test
    public void createTokenWithMultiPaths_whenCheckingForEndsWithPassingEmptyPath_thenShouldReturnFalse() {
        assertThat(make("firstPath/secondPath/thirdPath").endsWithPath("")).isFalse();
    }

    @Test
    public void createTokenWithMultiPaths_whenCheckingForEndsWithPassingNullPath_thenShouldReturnFalse() {
        assertThat(make("firstPath/secondPath/thirdPath").endsWithPath(null)).isFalse();
    }

    @Test
    public void createTokenWithMultiPaths_whenCheckingForEndsWithPassingLongerPath_thenShouldReturnFalse() {
        assertThat(make("firstPath/secondPath/thirdPath").endsWithPath("firstPath/secondPath/thirdPath/fourthPath"))
                .isFalse();
    }

    //--------------------------

    @Test
    public void givenStateHistoryToken_whenCheckingForContainsNullPath_thenShouldReturnFalse() {
        assertThat(make("firstPath/secondPath/thirdPath").containsPath(null)).isFalse();
    }

    @Test
    public void givenStateHistoryToken_whenCheckingForContainsEmptyPath_thenShouldReturnFalse() {
        assertThat(make("firstPath/secondPath/thirdPath").containsPath("")).isFalse();
    }

    @Test
    public void givenStateHistoryToken_whenCheckingForContainsASinglePathThatExist_thenShouldReturnTrue() {
        assertThat(make("firstPath/secondPath/thirdPath").containsPath("firstPath")).isTrue();
        assertThat(make("firstPath/secondPath/thirdPath").containsPath("secondPath")).isTrue();
        assertThat(make("firstPath/secondPath/thirdPath").containsPath("thirdPath")).isTrue();
    }

    @Test
    public void givenStateHistoryToken_whenCheckingForContainsMultiPathsThatNonExist_thenShouldReturnFalse() {
        assertThat(make("firstPath/secondPath/thirdPath/forthPath").containsPath("firstPath/nonExist")).isFalse();
    }

    @Test
    public void givenStateHistoryToken_whenCheckingForContainsMultiPathsThatExist_thenShouldReturnTrue() {
        assertThat(make("firstPath/secondPath/thirdPath/forthPath").containsPath("firstPath/secondPath")).isTrue();
        assertThat(make("firstPath/secondPath/thirdPath/forthPath").containsPath("secondPath/thirdPath")).isTrue();
        assertThat(make("firstPath/secondPath/thirdPath/forthPath").containsPath("thirdPath/forthPath")).isTrue();
        assertThat(make("firstPath/secondPath/thirdPath/forthPath").containsPath("secondPath/thirdPath/forthPath"))
                .isTrue();
        assertThat(make("firstPath/secondPath/thirdPath/forthPath").containsPath("secondPath/thirdPath/forthPath"))
                .isTrue();
        assertThat(make("firstPath/secondPath/thirdPath/forthPath")
                .containsPath("firstPath/secondPath/thirdPath/forthPath")).isTrue();
        assertThat(make("firstPath/secondPath/thirdPath/firstPath/forthPath").containsPath("firstPath/forthPath"))
                .isTrue();
    }

    //-------------------------------


    @Test
    public void givenAnEmptyStateHistoryToken_thenPathShouldBeEmpty() {
        assertThat("").isEqualTo(make("").path());
    }

    @Test
    public void givenAnSinglePathStateHistoryToken_thenPathShouldBeSinglePath() {
        assertThat("firstPath").isEqualTo(make("firstPath").path());
    }

    @Test
    public void givenMultiPathStateHistoryToken_thenPathShouldBeTheMultiPathsString() {
        assertThat("firstPath/secondPath").isEqualTo(make("firstPath/secondPath").path());
    }

    @Test
    public void givenMultiPathStateHistoryTokenWithQueryParameters_thenPathShouldBeTheMultiPathsString() {
        assertThat("firstPath/secondPath").isEqualTo(make("firstPath/secondPath?param=value").path());
    }

    @Test
    public void givenMultiPathStateHistoryTokenWithUrlFragments_thenPathShouldBeTheMultiPathsString() {
        assertThat("firstPath/secondPath").isEqualTo(make("firstPath/secondPath#fragment").path());
        assertThat("firstPath/secondPath").isEqualTo(make("firstPath/secondPath/#fragment").path());
        assertThat("firstPath/secondPath").isEqualTo(make("firstPath/secondPath/!#fragment").path());
        assertThat("firstPath/secondPath").isEqualTo(make("firstPath/secondPath?param=value#fragment").path());
    }

    @Test
    public void givenEmptyStateHistoryToken_thenPathsShouldBeEmpty() throws Exception {
        assertThat(make("").paths()).isEmpty();
    }

    @Test
    public void givenSinglePathStateHistoryToken_thenPathsShouldBeTheFirstPathEntry() throws Exception {
        assertThat(make("firstPath").paths()).containsExactly("firstPath");
        assertThat(make("/firstPath").paths()).containsExactly("firstPath");
        assertThat(make("/firstPath/").paths()).containsExactly("firstPath");
        assertThat(make("firstPath/").paths()).containsExactly("firstPath");
    }

    @Test
    public void givenMultiPathStateHistoryToken_thenPathsShouldContainsAllPathsEntries() throws Exception {
        assertThat(make("firstPath/secondPath/thirdPath").paths())
                .containsExactly("firstPath", "secondPath", "thirdPath");
        assertThat(make("/firstPath/secondPath/thirdPath").paths())
                .containsExactly("firstPath", "secondPath", "thirdPath");
        assertThat(make("/firstPath/secondPath/thirdPath/").paths())
                .containsExactly("firstPath", "secondPath", "thirdPath");
        assertThat(make("firstPath/secondPath/thirdPath/").paths())
                .containsExactly("firstPath", "secondPath", "thirdPath");
    }

    @Test
    public void givenAnEmptyPathStateHistoryToken_thenQueryShouldReturnEmptyQuery() throws Exception {
        assertThat(make("").query()).isEmpty();
    }

    @Test
    public void givenSinglePathStateHistoryToken_thenQueryShouldReturnEmptyQuery() throws Exception {
        assertThat(make("firstPath").query()).isEmpty();
    }

    @Test
    public void givenPathWithParameterStateHistoryToken_thenQueryShouldReturnTheParamtersString() throws Exception {
        assertThat(make("firstPath?param=value").query()).isEqualTo("param=value");
    }


    @Test
    public void givenPathWithParameterAndUrlFragmentStateHistoryToken_thenQueryShouldReturnTheParamtersStringOnly()
            throws Exception {
        assertThat(make("firstPath?param=value#fragment").query()).isEqualTo("param=value");
    }

    @Test
    public void givenStateHistoryToken_whenAppendPath_thenTheNewPathShouldEndWithTheAppendedPath()
            throws Exception {
        assertThat(make("").appendPath("firstPath").path()).isEqualTo("firstPath");
        assertThat(make("").appendPath(null).path()).isEqualTo("null");
        assertThat(make("firstPath").appendPath("secondPath").path()).isEqualTo("firstPath/secondPath");
        assertThat(make("firstPath?param=value").appendPath("secondPath").path()).isEqualTo("firstPath/secondPath");
        assertThat(make("firstPath").appendPath("secondPath/thirdPath").path())
                .isEqualTo("firstPath/secondPath/thirdPath");
        assertThat(make("firstPath").appendPath("secondPath?param=value").path()).isEqualTo("firstPath/secondPath");
        assertThat(make("firstPath").appendPath("secondPath?param=value#fragment").path())
                .isEqualTo("firstPath/secondPath");
    }

    @Test
    public void givenStateHistoryToken_whenReplacePath_thenThePathShouldBeReplacedWithTheReplacement()
            throws Exception {
        assertThat(make("").replacePath("firstPath", "secondPath").path()).isEmpty();
        assertThat(make("firstPath").replacePath("firstPath", "secondPath").path()).isEqualTo("secondPath");
        assertThat(make("firstPath/secondPath").replacePath("secondPath", "thirdPath").path())
                .isEqualTo("firstPath/thirdPath");
        assertThat(make("firstPath/secondPath").replacePath("secondPath", "thirdPath/forthPath").path())
                .isEqualTo("firstPath/thirdPath/forthPath");
        assertThat(make("firstPath/secondPath").replacePath("firstPath/secondPath", "thirdPath").path())
                .isEqualTo("thirdPath");
        assertThat(make("firstPath/secondPath").replacePath("secondPath", "thirdPath?param=value").path())
                .isEqualTo("firstPath/thirdPath");
        assertThat(make("firstPath/secondPath").replacePath("secondPath", "thirdPath?param=value#fragment").path())
                .isEqualTo("firstPath/thirdPath");
    }


    @Test
    public void givenStateHistoryToken_whenReplaceLastPath_thenThePathShouldBeReplacedWithTheReplacement()
            throws Exception {
        assertThat(make("").replaceLastPath("secondPath").path()).isEmpty();
        assertThat(make("firstPath").replaceLastPath("secondPath").path()).isEqualTo("secondPath");
        assertThat(make("firstPath/secondPath").replaceLastPath("thirdPath").path()).isEqualTo("firstPath/thirdPath");
    }
}
