package enhancer;

public final class Namers {
  private Namers() {
    /* Static utility */
  }
  
  public static Namer suffixNamer(String suffix) {
    final String FULL_SUFFIX = "$" + suffix;
    return new Namer() {
      @Override
      public String map(String enhanced) {
        return enhanced + FULL_SUFFIX;
      }

      @Override
      public String unmap(String name) {
        int i = name.lastIndexOf(FULL_SUFFIX);
        return i > 0 ? name.substring(0, i) : null;
      }
    };
  }
}
