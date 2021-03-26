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
package org.dominokit.domino.service.discovery.configuration;

import io.vertx.core.json.JsonObject;

public class HttpEndpointConfiguration extends BaseServiceConfiguration {
  private final String host;
  private String root = "/";
  private int port = 80;
  private boolean ssl = false;

  public HttpEndpointConfiguration(String name, String host) {
    super(name);
    if (!isValid(host)) throw new InvalidHttpEndpointHostException();
    this.host = host;
  }

  public String getHost() {
    return host;
  }

  public HttpEndpointConfiguration root(String root) {
    this.root = root;
    return this;
  }

  public HttpEndpointConfiguration port(int port) {
    this.port = port;
    return this;
  }

  public HttpEndpointConfiguration ssl(boolean ssl) {
    this.ssl = ssl;
    return this;
  }

  public boolean isSsl() {
    return ssl;
  }

  public String getRoot() {
    return root;
  }

  public int getPort() {
    return port;
  }

  @Override
  public HttpEndpointConfiguration metadata(JsonObject metadata) {
    this.metadata = metadata;
    return this;
  }

  public static class InvalidHttpEndpointHostException extends RuntimeException {}
}
