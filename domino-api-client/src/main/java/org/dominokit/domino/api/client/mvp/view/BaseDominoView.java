package org.dominokit.domino.api.client.mvp.view;

import org.dominokit.domino.api.shared.extension.Content;

public abstract class BaseDominoView<T> implements DominoView<T> {

    private boolean initialized = false;
    protected RevealedHandler revealHandler;
    protected RemovedHandler removeHandler;

    protected T root;

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    @Override
    public Content getContent() {
        if(!initialized || !isSingleton()){
            root = createRoot();
            initRoot(root);
            init(root);
        }
        return (Content<T>)() -> root;
    }

    protected abstract void initRoot(T root);
    public abstract void init(T root);

    @Override
    public void clear() {

    }

    @Override
    public void setRevealHandler(RevealedHandler revealHandler) {
        this.revealHandler = revealHandler;
    }

    @Override
    public void setRemoveHandler(RemovedHandler removeHandler) {
        this.removeHandler = removeHandler;
    }
}
