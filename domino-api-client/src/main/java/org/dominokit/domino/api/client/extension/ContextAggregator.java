package org.dominokit.domino.api.client.extension;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public class ContextAggregator {

    private Set<ContextWait> contextsSet=new LinkedHashSet<>();

    private ContextAggregator(Set<ContextWait> contextSet,
                              ReadyHandler handler) {
        this.contextsSet=contextSet;
        this.contextsSet.forEach(c-> c.onReady(() -> {
            this.contextsSet.remove(c);
            if(this.contextsSet.isEmpty())
                handler.onReady();
        }));
    }

    public static CanWaitForContext waitFor(ContextWait context){
        return ContextAggregatorBuilder.waitForContext(context);
    }

    public static CanWaitForContext waitFor(Collection<ContextWait> contexts){
        return ContextAggregatorBuilder.waitForContext(contexts);
    }

    public interface CanWaitForContext{
        CanWaitForContext and(ContextWait context);
        ContextAggregator onReady(ReadyHandler handler);
    }

    @FunctionalInterface
    public interface ReadyHandler{
        void onReady();
    }

    private static class ContextAggregatorBuilder implements CanWaitForContext {

        private Set<ContextWait> contextSet=new LinkedHashSet<>();

        private ContextAggregatorBuilder(ContextWait context) {
            this.contextSet.add(context);
        }

        private ContextAggregatorBuilder(Collection<ContextWait> contexts) {
            this.contextSet.addAll(contexts);
        }

        public static CanWaitForContext waitForContext(ContextWait context){
            return new ContextAggregatorBuilder(context);
        }

        public static CanWaitForContext waitForContext(Collection<ContextWait> contexts){
            return new ContextAggregatorBuilder(contexts);
        }

        @Override
        public CanWaitForContext and(ContextWait context) {
            this.contextSet.add(context);
            return this;
        }

        @Override
        public ContextAggregator onReady(ReadyHandler handler) {
            return new ContextAggregator(contextSet, handler);
        }
    }

    public static class ContextWait<T>{
        private Set<ReadyHandler> readyHandlers=new LinkedHashSet<>();
        private T context;

        public static <T> ContextWait<T> create(){
            return new ContextWait<>();
        }

        void onReady(ReadyHandler handler){
            this.readyHandlers.add(handler);
        }

        public void receiveContext(T context){
            this.context=context;
            readyHandlers.forEach(ReadyHandler::onReady);
        }

        public T get(){
            return context;
        }
    }
}
