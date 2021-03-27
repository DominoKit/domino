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
package org.dominokit.domino.apt.client.processors.module.client.initialtasks;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.dominokit.domino.api.client.InitialTaskRegistry;
import org.dominokit.domino.apt.commons.AbstractRegisterMethodWriter;

public class RegisterInitialTasksMethodWriter
    extends AbstractRegisterMethodWriter<InitialTaskEntry, String> {

  public RegisterInitialTasksMethodWriter(TypeSpec.Builder clientModuleTypeBuilder) {
    super(clientModuleTypeBuilder);
  }

  @Override
  protected String methodName() {
    return "registerInitialTasks";
  }

  @Override
  protected Class<?> registryClass() {
    return InitialTaskRegistry.class;
  }

  @Override
  protected void registerItem(InitialTaskEntry entry, MethodSpec.Builder methodBuilder) {
    methodBuilder.addStatement(
        "registry.registerInitialTask(new $T())", ClassName.bestGuess(entry.initalTask));
  }

  @Override
  protected InitialTaskEntry parseEntry(String item) {
    return new InitialTaskEntry(item);
  }
}
