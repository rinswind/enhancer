package edu.unseen.osgi.enhancer;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;

public class Enhancer {
  private final ClassLoader privateSpace;
  private final Namer namer;
  private final Generator generator;
  private final Map<ClassLoader, WeakReference<ClassLoader>> cache;

  public Enhancer(ClassLoader privateSpace, Namer naming, Generator generator) {
    this.privateSpace = privateSpace;
    this.namer = naming;
    this.generator = generator;
    this.cache = new WeakHashMap<ClassLoader, WeakReference<ClassLoader>>();
  }

  @SuppressWarnings("unchecked")
  public <T> Class<T> enhance(Class<T> target) throws ClassNotFoundException {
    ClassLoader context = resolveBridge(target.getClassLoader());
    String enhancementName = namer.map(target.getName());
    return (Class<T>) context.loadClass(enhancementName);
  }
  
  private synchronized ClassLoader resolveBridge(ClassLoader appSpace) {
    ClassLoader bridge = null;

    WeakReference<ClassLoader> ref = cache.get(appSpace);
    if (ref != null) {
      bridge = ref.get();
    }

    if (bridge == null) {
      bridge = makeBridge(appSpace);
      cache.put(appSpace, new WeakReference<ClassLoader>(bridge));
    }

    return bridge;
  }
  
  private ClassLoader makeBridge(final ClassLoader appSpace) {
    return new ClassLoader(appSpace) {
      @Override
      public String toString() {
        return "BridgeClassLoader[ " + appSpace + ", " + privateSpace + " ]";
      }
      
      @Override
      protected Class<?> findClass(String name) throws ClassNotFoundException {
        /* Is this used privately by the enhancements? */
        if (generator.isImplClass(name)) {
          return privateSpace.loadClass(name);
        }

        /* Is this a request for enhancement? */
        String unpacked = namer.unmap(name);
        if (unpacked != null) {
          byte[] raw = generator.generate(unpacked, name, this);
          return defineClass(name, raw, 0, raw.length);
        }

        /* Ask someone else */
        throw new ClassNotFoundException(name);
      }
    };    
  }
}
