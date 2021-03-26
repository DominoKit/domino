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
package org.dominokit.domino.remote.logging;

import com.google.auto.service.AutoService;
import com.google.gwt.core.server.StackTraceDeobfuscator;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.dominokit.domino.api.server.logging.RemoteLogger;

@AutoService(RemoteLogger.class)
public class DeobfuscatorRemoteLogger implements RemoteLogger {

  private static final Logger LOGGER = Logger.getLogger(DeobfuscatorRemoteLogger.class.getName());
  private StackTraceDeobfuscator deobfuscator = new DominoStackTraceDeobfuscator();

  @Override
  public void log(LogRecord logRecord, String permutationStrongName) {
    if (isError(logRecord))
      deobfuscator.deobfuscateStackTrace(logRecord.getThrown(), permutationStrongName);
    LOGGER.log(logRecord);
  }

  private boolean isError(LogRecord logRecord) {
    return logRecord.getLevel().equals(Level.SEVERE);
  }
}
