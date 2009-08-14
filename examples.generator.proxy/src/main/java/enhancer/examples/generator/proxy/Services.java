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
package enhancer.examples.generator.proxy;

import org.osgi.framework.BundleContext;

import enhancer.Enhancer;
import enhancer.Namers;
import enhancer.examples.generator.proxy.internal.ServiceProxyGenerator;

public final class Services {
  /* Static utility */
  private Services() {
  }
  
  /**
   * Build an Enhancer on top of the current class space. E.g. the class space
   * of the bundle where this class is packaged. The created proxy classes will
   * have a suffix "$__service_proxy__".
   */
  private static final Enhancer enhancer = new Enhancer(
    Services.class.getClassLoader(), 
    Namers.suffixNamer("__service_proxy__"), 
    new ServiceProxyGenerator());

  public static <T> T service(Class<T> target, BundleContext bc) {
    try {
      Class<T> wrapperClass = enhancer.enhance(target);
      return wrapperClass.getConstructor(BundleContext.class).newInstance(bc);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
