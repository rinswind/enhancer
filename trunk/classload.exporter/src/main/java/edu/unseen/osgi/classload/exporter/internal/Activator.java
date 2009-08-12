package edu.unseen.osgi.classload.exporter.internal;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import edu.unseen.osgi.classload.exporter.goodbie.Goodbie;
import edu.unseen.osgi.classload.exporter.message.Message;

public class Activator implements BundleActivator {
  @Override
  public void start(BundleContext context) throws Exception {
    context.registerService(Goodbie.class.getName(), new Goodbie() {
      @Override
      public Message hello(final String who) {
        return new Message() {
          @Override
          public String toString() {
            return "Hello " + who;
          }
        };
      }

      @Override
      public Message goodbie(final String who) {
        return new Message() {
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
