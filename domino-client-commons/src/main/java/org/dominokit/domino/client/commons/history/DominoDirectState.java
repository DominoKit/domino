package org.dominokit.domino.client.commons.history;

import org.dominokit.domino.api.shared.history.NormalizedToken;
import org.dominokit.domino.api.shared.history.StateHistoryToken;
import org.dominokit.domino.api.shared.history.TokenFilter;

import java.util.function.Consumer;

import static org.dominokit.domino.api.shared.history.DominoHistory.*;

public class DominoDirectState implements DirectState {

    private final TokenFilter tokenFilter;
    private final State state;
    private StateListener listener;
    private Consumer<DominoDirectState> onCompleted;

    public DominoDirectState(TokenFilter tokenFilter, State state, StateListener listener) {
        this.tokenFilter = tokenFilter;
        this.state = state;
        this.listener = listener;
    }

    public DominoDirectState onCompleted(Consumer<DominoDirectState> onCompleted) {
        this.onCompleted = onCompleted;
        return this;
    }

    @Override
    public void onDirectUrl() {
        onDirectUrl(tokenFilter);
    }

    @Override
    public void onDirectUrl(TokenFilter tokenFilter) {
        NormalizedToken normalized = tokenFilter.normalizeToken(state.token().value());
        state.setNormalizedToken(normalized);
        if (tokenFilter.filter(new StateHistoryToken(normalized.getToken().value()))) {
            listener.onPopState(state);
            onCompleted.accept(this);
        }
    }
}