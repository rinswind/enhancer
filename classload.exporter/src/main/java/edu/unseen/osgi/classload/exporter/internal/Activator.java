package edu.unseen.osgi.classload.exporter.internal;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import edu.unseen.osgi.classload.exporter.goodbie.Goodbie;
import edu.unseen.osgi.classload.exporter.goodbie.GoodbieMessage;
import edu.unseen.osgi.classload.exporter.hello.HelloMessage;

public class Activator implements BundleActivator {
  @Override
  public void start(BundleContext context) throws Exception {
    context.registerService(Goodbie.class.getName(), new Goodbie() {
      @Override
      public HelloMessage hello(final String who) {
        return new HelloMessage() {
          @Override
          public String toString() {
            return "Hello " + who;
          }
        };
      }

      @Override
      public GoodbieMessage goodbie(final String who) {
        return new GoodbieMessage() {
          @Override
          public String toString() {
            return "Goodbie " + who;
          }
        };
      }
    }, null);
  }

  @Override
  public void stop(BundleContext context) throws Exception {
  }
}
