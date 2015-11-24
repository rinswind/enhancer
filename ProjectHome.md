## Intro ##

A simple library to help in the building of class generation applications under OSGi.
This is not a code generation framework - there are plenty of those already. Instead this library controls when classes are produced by a pluggable code generator and manages the class loading environment into, which the classes can safely be defined and linked.

Currently this project is intended as an introduction to advanced class loading and code generation under OSGi. It is functional but not refined to "production ready" status.

## Contents ##

The project consists of 1 main bundle and 6 demo/test bundles:

  * `enhancer.lib`
> Contains the core micro-framework for class loader management.

  * `asm`
> Bundelized ASM used by the demo code generators.

  * `examples.exporter`
> Exports a single service object and it's API. The API is deliberately distributed between two packages. The first package contains a basic `Hello` service. The second package contains an interface `Goodbye` that extends `Hello`. The service is exported under the `Goodbye` interface. Clients that call only methods declared directly by `Goodbye` do not need to import anything from the package of `Hello`. Dynamic proxies on the other hand typically override all methods - declared and inherited. Thus we induce a condition where the client of the service does not see enough classes to link a proxy class that overrides all the methods of the service.

  * `examples.generator.aop`
> Generates AOP-like wrappers that log on the console all invocations to the wrapped object. Uses a cache of classload bridges. Note that this generator has no private implementation to support it's proxies - it only needs the classes required to do `System.out.println()`.

  * `examples.generator.proxy`
> Generates proxies to track dynamic OSGi services. The proxies delegate the heavy lifting to an internal implementation class. The classload bridge is configured to strictly separate the proxied class space from the internal implementation space.

  * `examples.importer`
> Builds a complex stack of proxies that use both code generators. Calls the proxy stack to demonstrate that it works. Prints on the console the names of the generated classes. Must be started after the examples.exporter bundle or blows with a `ServiceUnavailableException` - a feature supported by the service tracking proxy.

  * `examples.importer.broken`
> Tries to use directly the AOP generator and to load the raw `byte[]` block via it's own `ClassLoader`. This induces a `ClassNotFoundException`. This happens because the set of classes needed to link the method calls done by `examples.importer.broken` is different from the set of classes needed to link the dynamic proxy. The proxy overrides all methods - declared and inherited, and typically needs to see more classes than an application that calls only a few of those methods.

## Prerequisites ##

  * Subversion
  * JDK 1.6
  * Maven 2.0

## Usage ##

  1. Checkout source.
  1. Run `mvn install` in the root directory.
  1. Go to `assembly/target/runtime/bin`.
  1. Execute the startup script that matches your platform. This will launch a Felix OSGi framework with all bundles installed.
  1. Use the Felix console commands to start and stop the bundles.
  1. Use the Felix console commands to examine the manifests and wires of the bundles to get a feeling of where the classload bridges do their thing.