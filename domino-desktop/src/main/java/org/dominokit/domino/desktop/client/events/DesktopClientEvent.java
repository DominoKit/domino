/*
 * Copyright © ${year} Dominokit
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
package org.dominokit.domino.desktop.client.events;

import org.dominokit.domino.api.client.ClientApp;
import org.dominokit.domino.api.client.request.PresenterCommand;
import org.dominokit.domino.rest.shared.Event;
import org.dominokit.domino.rest.shared.request.Request;

public class DesktopClientEvent implements Event {

  private final PresenterCommand request;

  public DesktopClientEvent(PresenterCommand request) {
    this.request = request;
  }

  @Override
  public void fire() {
    ClientApp.make().getEventsBus().publishEvent(() -> this);
  }

  @Override
  public void process() {
    request.applyState(new Request.DefaultRequestStateContext());
  }
}
