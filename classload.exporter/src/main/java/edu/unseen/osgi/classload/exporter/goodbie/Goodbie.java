package edu.unseen.osgi.classload.exporter.goodbie;

import edu.unseen.osgi.classload.exporter.hello.Hello;

public interface Goodbie extends Hello {
  GoodbieMessage goodbie(String who);
}
