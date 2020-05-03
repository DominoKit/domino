package org.dominokit.domino.gwt.client.extensions;

import elemental2.dom.EventListener;
import elemental2.dom.*;
import jsinterop.base.Js;
import org.dominokit.domino.api.client.ClientApp;
import org.dominokit.domino.api.client.extension.DominoEventsListenersRepository;
import org.dominokit.domino.api.client.extension.InMemoryDominoEventsListenerRepository;
import org.dominokit.domino.api.shared.extension.DominoEvent;
import org.dominokit.domino.api.shared.extension.DominoEventListener;
import org.dominokit.domino.api.shared.extension.GlobalDominoEventListener;
import org.dominokit.domino.api.shared.extension.GlobalEvent;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

public class CustomEventsDominoEventsRepository implements DominoEventsListenersRepository {

    private final Map<String, List<ListenerWrapper>> listeners = new HashMap<>();
    private final Map<String, List<GlobalListenerWrapper>> globalListeners = new HashMap<>();

    @Override
    public void addListener(Class<? extends DominoEvent> dominoEvent, DominoEventListener dominoEventListener) {
        initializeEventListeners(dominoEvent);
        ListenerWrapper wrapper = new ListenerWrapper(dominoEventListener);
        listeners.get(dominoEvent.getCanonicalName()).add(wrapper);
    }

    @Override
    public void addGlobalListener(Class<? extends DominoEvent> dominoEvent, GlobalDominoEventListener dominoEventListener) {
        initializeGlobalEventListeners(dominoEvent);
        GlobalListenerWrapper wrapper = new GlobalListenerWrapper(dominoEventListener);
        globalListeners.get(dominoEvent.getCanonicalName()).add(wrapper);
        DomGlobal.document.addEventListener(dominoEvent.getCanonicalName(), wrapper.dominoCustomEventListener);
    }

    @Override
    public Set<DominoEventListener> getEventListeners(Class<? extends DominoEvent> dominoEvent) {
        initializeEventListeners(dominoEvent);
        return listeners.get(dominoEvent.getCanonicalName()).stream().map(cw -> cw.dominoEventListener).collect(
                Collectors.toSet());
    }

    @Override
    public void fireEvent(Class<? extends DominoEvent> eventType, DominoEvent dominoEvent) {
        if(dominoEvent instanceof GlobalEvent){
            CustomEventInit customEventInit= CustomEventInit.create();
            customEventInit.setDetail(((GlobalEvent) dominoEvent).serialize());
            CustomEvent customEvent = new CustomEvent(eventType.getCanonicalName(), customEventInit);
            DomGlobal.document.dispatchEvent(customEvent);
        }else{
            getEventListeners(eventType)
                    .forEach(listener ->
                            ClientApp.make().getAsyncRunner().runAsync(() -> listener.onEventReceived(dominoEvent)));
        }
    }

    private List<ListenerWrapper> getEventListenersWrapper(Class<? extends DominoEvent> dominoEvent) {
        initializeEventListeners(dominoEvent);
        return listeners.get(dominoEvent.getCanonicalName());
    }

    private List<GlobalListenerWrapper> getGlobalEventListenersWrapper(Class<? extends DominoEvent> dominoEvent) {
        initializeGlobalEventListeners(dominoEvent);
        return globalListeners.get(dominoEvent.getCanonicalName());
    }

    @Override
    public void removeListener(Class<? extends DominoEvent> event, DominoEventListener listener) {
        List<ListenerWrapper> eventListeners = getEventListenersWrapper(event);
        eventListeners.remove(new ListenerWrapper(listener));

        if(eventListeners.isEmpty()){
            listeners.remove(event.getCanonicalName());
        }
    }

    @Override
    public void removeGlobalListener(Class<? extends DominoEvent> event, GlobalDominoEventListener listener) {
        List<GlobalListenerWrapper> eventListeners = getGlobalEventListenersWrapper(event);
        int index = eventListeners.indexOf(new GlobalListenerWrapper(listener));
        if (index >= 0){
            GlobalListenerWrapper tobeRemoved = eventListeners.get(index);
            eventListeners.remove(tobeRemoved);
            DomGlobal.document.removeEventListener(event.getCanonicalName(), tobeRemoved.dominoCustomEventListener);
            DomGlobal.console.info("Event listener removed : "+event.getCanonicalName()+ " : "+ listener.getClass().getCanonicalName());
        }

        if (eventListeners.isEmpty()) {
            globalListeners.remove(event.getCanonicalName());
        }
    }

    private void initializeEventListeners(Class<? extends DominoEvent> extensionPoint) {
        if (isNull(listeners.get(extensionPoint.getCanonicalName())))
            listeners.put(extensionPoint.getCanonicalName(), new LinkedList<>());
    }

    private void initializeGlobalEventListeners(Class<? extends DominoEvent> extensionPoint) {
        if (isNull(globalListeners.get(extensionPoint.getCanonicalName())))
            globalListeners.put(extensionPoint.getCanonicalName(), new LinkedList<>());
    }

    public static class DominoCustomEventListener implements EventListener {

        private final DominoEventListener dominoEventListener;

        public DominoCustomEventListener(DominoEventListener dominoEventListener) {
            this.dominoEventListener = dominoEventListener;
        }

        @Override
        public void handleEvent(Event evt) {
            CustomEvent customEvent = Js.uncheckedCast(evt);
            if(dominoEventListener instanceof GlobalDominoEventListener){
                DominoEvent event = ((GlobalDominoEventListener) dominoEventListener).deserializeEvent((String) customEvent.detail);
                dominoEventListener.onEventReceived(event);
            }

        }
    }

    private class GlobalListenerWrapper extends ListenerWrapper {
        private final DominoCustomEventListener dominoCustomEventListener;

        public GlobalListenerWrapper(DominoEventListener dominoEventListener) {
            super(dominoEventListener);
            this.dominoCustomEventListener = new DominoCustomEventListener(dominoEventListener);
        }
    }

    private class ListenerWrapper {

        private final DominoEventListener dominoEventListener;

        public ListenerWrapper(DominoEventListener dominoEventListener) {
            this.dominoEventListener = dominoEventListener;
        }

        @Override
        public boolean equals(Object other) {
            if (isNull(other))
                return false;
            return dominoEventListener.getClass().getCanonicalName().equals(((ListenerWrapper) other).dominoEventListener.getClass().getCanonicalName());
        }

        @Override
        public int hashCode() {
            return dominoEventListener.getClass().getCanonicalName().hashCode();
        }
    }
}
