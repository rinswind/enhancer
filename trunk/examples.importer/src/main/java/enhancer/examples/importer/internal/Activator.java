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
package enhancer.examples.importer.internal;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import enhancer.examples.exporter.goodbye.Goodbye;
import enhancer.examples.generator.aop.Logging;
import enhancer.examples.generator.proxy.ServiceProxies;

public class Activator implements BundleActivator {
  public void start(BundleContext bc) throws Exception {
    /* Build a service proxy */
    Class<Goodbye> serviceProxyClass = ServiceProxies.serviceProxy(Goodbye.class);
    Goodbye serviceProxy = serviceProxyClass.getConstructor(BundleContext.class).newInstance(bc);
    
    /* Wrap it in a logging proxy */
    Class<Goodbye> logProxyClass = Logging.withLogging(Goodbye.class);
    Goodbye logProxy = logProxyClass.getConstructor(Goodbye.class).newInstance(serviceProxy);
    
    /* Drive the proxy stack */
    System.out.println(logProxy.goodbie("importer"));
  }

  public void stop(BundleContext bc) throws Exception {
  }
}
