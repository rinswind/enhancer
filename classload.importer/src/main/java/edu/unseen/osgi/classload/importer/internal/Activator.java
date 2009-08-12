package edu.unseen.osgi.classload.importer.internal;

import java.lang.reflect.Method;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import edu.unseen.osgi.classload.exporter.goodbie.Goodbie;
import edu.unseen.osgi.enhancer.Enhancer;
import edu.unseen.osgi.enhancer.Namer;
import edu.unseen.osgi.enhancer.Namers;

public class Activator implements BundleActivator {
  public void start(BundleContext context) throws Exception {
    tryBridgeLoader(context);
    tryLocalLoader(context);
  }

  private void tryBridgeLoader(BundleContext bc) throws Exception {
    /* Build a class load bridge on top of this bundles class loader */
    ClassLoader local = getClass().getClassLoader();
    Namer namer = Namers.suffixNamer("__proxy__");
    PassthroughGenerator generator = new PassthroughGenerator();
    Enhancer enhancer = new Enhancer(local, namer, generator);
    
    /* Make a proxy class */
    Class<Goodbie> proxyClass = enhancer.enhance(Goodbie.class);
    
    /* Drive it */
    driveProxy(bc, proxyClass);
  }
  
  private void tryLocalLoader(BundleContext bc) throws Exception {
    /* Generate the raw bytes through this bundles' class loader */
    ClassLoader local = getClass().getClassLoader();
    String inputName = Goodbie.class.getName();
    String outputName = inputName + "$__broken__";
    byte[] raw = new PassthroughGenerator().generate(inputName, outputName, local);
    
    /* Must reach with reflection - defineClass is protected */
    Method meth = ClassLoader.class.getDeclaredMethod(
        "defineClass", String.class, byte[].class, int.class, int.class);
    meth.setAccessible(true);
    
    /* Make a proxy class */
    @SuppressWarnings("unchecked")
    Class<Goodbie> proxyClass = (Class<Goodbie>) meth.invoke(local, outputName, raw, 0, raw.length);
    
    /* Drive it */
    driveProxy(bc, proxyClass);
  }
  
  private void driveProxy(BundleContext bc, Class<Goodbie> proxyClass) throws Exception {
    System.out.printf("----- Start [ class: %s, loader: %s ] -----\n", proxyClass, proxyClass.getClassLoader());
    try {
      ServiceReference ref = bc.getServiceReference(Goodbie.class.getName());
      Goodbie delegate = (Goodbie) bc.getService(ref);
      
      Goodbie proxy = proxyClass.getConstructor(Goodbie.class).newInstance(delegate);
      
      /*
       * Notice we DO NOT call the "hello" method that Goodbie has inherited
       * from Hello. If we did call it than this bundle would have to import the
       * Hello package in order to link this activator. Than it would not matter
       * if we generate on top of the local loader or the bridge because both
       * will see the same classes.
       */
      System.out.println(proxy.goodbie("importer"));
    } catch (Throwable e) {
      e.printStackTrace();
    } finally {
      System.out.println("----- End -----");
    }
  }

  public void stop(BundleContext context) throws Exception {
  }
}