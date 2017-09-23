package com.progressoft.brix.domino.client.commons.history;

import com.progressoft.brix.domino.api.shared.history.TokenFilter;

import static com.progressoft.brix.domino.api.shared.history.DominoHistory.*;

public class DominoDirectState implements DirectState {

    private final TokenFilter tokenFilter;
    private final State state;

    public DominoDirectState(TokenFilter tokenFilter, State state) {
        this.tokenFilter = tokenFilter;
        this.state = state;
    }

    @Override
    public void onDirectUrl(DirectUrlHandler handler) {
        if (tokenFilter.filter(state.token().value()))
            handler.handle(state);
    }
}