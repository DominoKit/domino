/*
 * Copyright © 2019 Dominokit
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dominokit.domino.test.api.client;

import java.util.HashMap;
import java.util.Map;
import org.dominokit.domino.api.client.extension.InMemoryDominoEventsListenerRepository;
import org.dominokit.domino.api.shared.extension.DominoEvent;
import org.dominokit.domino.api.shared.extension.DominoEventListener;

public class TestInMemoryEventsListenersRepository extends InMemoryDominoEventsListenerRepository {

  protected final Map<String, DominoEventListener> testListeners = new HashMap<>();

  @Override
  public void addListener(
      Class<? extends DominoEvent> dominoEvent, DominoEventListener dominoEventListener) {
    super.addListener(dominoEvent, dominoEventListener);
    testListeners.put(dominoEventListener.getClass().getCanonicalName(), dominoEventListener);
  }

  public <L extends DominoEventListener> L getListener(Class<L> listenerClass) {
    return (L) testListeners.get(listenerClass.getCanonicalName());
  }
}
