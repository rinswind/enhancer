package enhancer.examples.importer.broken.internal;

import java.lang.reflect.Method;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import enhancer.examples.exporter.goodbye.Goodbye;
import enhancer.examples.generator.aop.LoggingGenerator;

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
    Method meth = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class,
        int.class, int.class);
    meth.setAccessible(true);

    /* Make a proxy class */
    @SuppressWarnings("unchecked")
    Class<Goodbye> proxyClass = (Class<Goodbye>) meth.invoke(local, outputName, raw, 0, raw.length);

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
    System.out.println(proxy.goodbie("importer"));
  }

  public void stop(BundleContext bc) throws Exception {
  }
}
