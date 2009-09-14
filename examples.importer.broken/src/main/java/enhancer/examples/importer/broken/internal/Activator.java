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
package enhancer.examples.importer.broken.internal;

import java.lang.reflect.Method;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import enhancer.examples.exporter.goodbye.Goodbye;
import enhancer.examples.generator.aop.internal.LoggingGenerator;

public class Activator implements BundleActivator {
  public void start(BundleContext bc) throws Exception {
    /*
     * Try to drive the generator directly. Load classes form this bundle's
     * class loader.
     */
    ClassLoader local = getClass().getClassLoader();
    String inputName = Goodbye.class.getName();
    String outputName = inputName + "$__broken__";
    byte[] raw = new LoggingGenerator().generate(inputName, outputName, local);

    /*
     * Must reach with reflection to call this bundle's class loader
     * defineClass() - it is a protected method.
     */
    Method defineClass = ClassLoader.class.getDeclaredMethod(
        "defineClass", String.class, byte[].class, int.class, int.class);
    defineClass.setAccessible(true);

    /* Make a proxy class */
    @SuppressWarnings("unchecked")
    Class<Goodbye> proxyClass = (Class<Goodbye>) defineClass.invoke(local, outputName, raw, 0, raw.length);

    /* Drive it */
    ServiceReference ref = bc.getServiceReference(Goodbye.class.getName());
    Goodbye delegate = (Goodbye) bc.getService(ref);

    Goodbye proxy = proxyClass.getConstructor(Goodbye.class).newInstance(delegate);

    /*
     * Notice we DO NOT call the "hello()" method that Goodbye has inherited
     * from Hello. If we did call it than this bundle would have to import
     * Hello's package in order to link this activator. Than it would not matter
     * if we generate on top of the local loader or the bridge because both will
     * see enough classes to link the generated proxy.
     */
    System.out.println(proxy.goodbye("importer"));
  }

  public void stop(BundleContext bc) throws Exception {
  }
}
