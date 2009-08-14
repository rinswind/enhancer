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

import static enhancer.examples.generator.aop.Logging.withLogging;
import static enhancer.examples.generator.proxy.Services.service;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import enhancer.examples.exporter.goodbye.Goodbye;

public class Activator implements BundleActivator {
  public void start(BundleContext bc) throws Exception {
    /*
     * Build a proxy stack that will track a service and also dump what is going
     * on on the console:
     * 
     * 1) wrap BundleContext in a logging aspect.
     * 
     * 2) build a service tracking proxy on top.
     * 
     * 3) wrap the proxy in a logging aspect as well.
     * 
     * Here we have two class load bridges playing together: one per generator.
     * It appears like we stack one bridge on top of the other but in fact we
     * bridge the owner of the Goodbye class twice. The reason we can't stack
     * bridges is that the generators do not support enhancement of classes. For
     * this reason we must pass to the logging generator an interface through,
     * which we want to see the wrapped object.
     */
    Goodbye stacked = 
      withLogging(Goodbye.class, service(Goodbye.class, withLogging(BundleContext.class, bc)));

    /* Drive the proxy stack */
    System.out.println(stacked.goodbye("importer"));
  }

  public void stop(BundleContext bc) throws Exception {
  }
}
