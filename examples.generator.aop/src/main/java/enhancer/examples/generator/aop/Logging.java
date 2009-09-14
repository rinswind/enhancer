/**
 * Copyright (C) 2009 Todor Boev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package enhancer.examples.generator.aop;

import enhancer.Enhancer;
import enhancer.Namers;
import enhancer.examples.generator.aop.internal.LoggingGenerator;

/**
 * Generates primitive logging wrappers over a Java interface.
 * 
 * @author rinsvind@gmail.com (Todor Boev)
 */
public final class Logging {
  /* Static utility */
  private Logging() {
  }

  /**
   * Build an Enhancer on top of the current class space. E.g. the class space
   * of the bundle where this class is packaged. The created proxy classes will
   * have a suffix "$__logging__".
   */
  private static final Enhancer enhancer = new Enhancer(
      Logging.class.getClassLoader(), 
      Namers.suffixNamer("__logging__"), 
      new LoggingGenerator());

  /**
   * Applies a logging aspect to an object.
   * 
   * @param <T> interface who's methods we want to capture and log.
   * @param <P> type of the object that we will wrap.
   * @param iface type token for <T> to counter erasure.
   * @param target the object we will be wrapping.
   * @return the passe object wrapped in a proxy that logs the calls to the
   *         passed interface.
   */
  public static <T, P extends T> T withLoggingFor(Class<T> iface, P target) {
    try {
      Class<T> wrapperClass = enhancer.enhance(iface);
      return (T) wrapperClass.getConstructor(iface).newInstance(target);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
