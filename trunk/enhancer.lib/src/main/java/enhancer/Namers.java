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
package enhancer;

/**
 * @author rinsvind@gmail.com (Todor Boev)
 */
public final class Namers {
  private Namers() {
    /* Static utility */
  }

  /**
   * Creates a suffix naming protocol. First "$" will be appended to the
   * original class name followed by user supplied suffix.
   * 
   * @param suffix suffix to append. Must be a valid Java identifier fragment.
   *          I.e. it can begin with a digit and than follow the normal Java
   *          identifier rules.
   * @return a {@link Namer} object.
   */
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
