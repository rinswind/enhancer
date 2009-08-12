package edu.unseen.osgi.classload.exporter.goodbie;

import edu.unseen.osgi.classload.exporter.hello.Hello;
import edu.unseen.osgi.classload.exporter.message.Message;

public interface Goodbie extends Hello {
  Message goodbie(String who);
}
