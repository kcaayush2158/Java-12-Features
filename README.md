# Java-12-Features


------------------------------------------------------------------------------------------
JEP 189: Shenandoah: A Low-Pause-Time Garbage Collector (Experimental)
JEP 230: Microbenchmark Suite
JEP 325: Switch Expressions
JEP 326: Raw String Literals (dropped from JDK 12 release)
JEP 334: JVM Constants API
JEP 340: One AArch64 Port, Not Two
JEP 341: Default CDS Archives
JEP 344: Abortable Mixed Collections for G1
JEP 346: Promptly Return Unused Committed Memory from G1
JEP 189: Shenandoah: A Low-Pause-Time Garbage Collector (Experimental)
Implemented and supported by RedHat for aarch64 and amd64, Shenandoah Garbage Collector, described in this paper, provides predictable and short GC pauses independent of the heap size.

It will be provided as an experimental feature, so in order to use it, -XX:+UnlockExperimentalVMOptions is needed together with -XX:+UseShenandoahGC.

Also, default (Oracle’s) OpenJDK builds will not contain this feature. You can use another build or make your own build.

On Oracle’s OpenJDK Early-Release build:

$ java -XX:+UnlockExperimentalVMOptions -XX:+UseShenandoahGC
Error occurred during initialization of VM
Option -XX:+UseShenandoahGC not supported
On my custom build, there is no error with these flags.

JEP 230: Microbenchmark Suite
A way, and a suite of microbenchmarks, to easily test the performance of JDK, based on Java Microbenchmark Harness (JMH) will be added to JDK source code.

It used like below, but these steps require you to have a system capable of building JDK from the source code.

$ cd jdk-src
$ sh make/devkit/createJMHBundle.sh
$ ./configure --with-jmh=build/jmh/jars --enable-headless-only
$ make test TEST="micro:java.lang.reflect"

... after many lines of output ...

Test selection 'micro:java.lang.reflect', will run:
* micro:java.lang.reflect

Running test 'micro:java.lang.reflect'
# JMH version: 1.21
# VM version: JDK 12-internal, OpenJDK 64-Bit Server VM, 12-internal+0-adhoc.ubuntu.jdk-src
# VM invoker: /home/ubuntu/jdk-src/build/linux-x86_64-server-release/images/jdk/bin/java
# VM options: --add-opens=java.base/java.io=ALL-UNNAMED
# Warmup: 5 iterations, 10 s each
# Measurement: 5 iterations, 10 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Average time, time/op
# Benchmark: org.openjdk.bench.java.lang.reflect.Clazz.getConstructor

# Run progress: 0.00% complete, ETA 01:31:40
# Fork: 1 of 5
# Warmup Iteration   1: 19.849 ns/op
# Warmup Iteration   2: 19.067 ns/op
# Warmup Iteration   3: 20.044 ns/op
# Warmup Iteration   4: 20.050 ns/op
# Warmup Iteration   5: 20.061 ns/op
Iteration   1: 20.037 ns/op
Iteration   2: 20.019 ns/op
Iteration   3: 20.070 ns/op
Iteration   4: 20.052 ns/op
Iteration   5: 20.024 ns/op

.. and continues running many more tests
JEP 325: Switch Expressions
This is a preview feature.

There are two main changes to switch in Java with this JEP:

Introduction of case L -> syntax that removes the need for break statements, because only the statements next to -> is executed.
switch can be an expression, so it can have a value, or it can return a value.
Example:

public class JEP325 {

	public static void main(String[] args) {

    // args[0] is the day of week, starting from 1-sunday
    final int day = Integer.valueOf(args[0]);

    // traditional switch
    switch (day) {
      case 2: 
      case 3: 
      case 4: 
      case 5: 
      case 6: 
        System.out.println("weekday");
        break;
      case 7:
      case 1:
        System.out.println("weekend");
        break;
      default:
        System.out.println("invalid");
    }

    // case L -> syntax
    // no break necessary, only code next to -> runs
    switch (day) {
      case 2, 3, 4, 5, 6 -> System.out.println("weekday");
      case 7, 1 -> System.out.println("weekend");
      default -> System.out.println("invalid");
    }

    // switch expression
    // then switch should be exhaustive if used as expression
    // break <value_of_switch_expression> syntax for blocks
    final String attr = switch (day) {
      case 2, 3, 4, 5, 6 -> "weekday";
      case 7, 1 -> "weekend";
      // it is possible to do this without a block and break
      // so default -> "invalid"; is actually enough here
      default -> {
        break "invalid";
      }
    };

    System.out.println(attr);

	}

}
Depending on the arg[0], this outputs the same string (weekday, weekend or invalid) three times.

JEP 326: Raw String Literals (dropped from JDK 12 release)
It has been dropped from JDK 12 release.

This is a preview feature.

Raw String Literals make it easy to use strings containing special characters and multi-line strings. Raw String Literals are created with backtick ` symbol. This JEP also introduces String::align function to make it easy to use multi-line indented text, and unescape/escape functions for conversions to/from (traditional) String Literals.

Example:

public class JEP326 {
  
  public static void main(String args[]) {

    // traditional string
    final String s1 = "test";
    // traditional multiline string
    final String s2 = "line1\nline2";

    // raw string literals
    final String rs1 = `test`;
    final String rs2 = `
                        line1
                          line2
                            line3`;
    final String rs3 = ``backtick`inside``;
    final String rs4 = `\n`;

    System.out.println(rs1);
    System.out.println(rs2);
    System.out.println(rs2.align());
    System.out.println(rs3);

    // String::unescape() is not implemented yet on jdk12+21
    System.out.println(rs4.length());
    // System.out.println(rs4.unescape().length());

  }

}
This outputs:

test

                        line1
                          line2
                            line3
line1
  line2
    line3

backtick`inside
2
JEP 334: JVM Constants API
JEP 334 proposes an API modeling the key class-file and run-time artifacts such as constant pool. Such API will contain classes like ConstantDesc, ClassDesc, and the draft of this API is available here: https://cr.openjdk.java.net/~vromero/constant.api/javadoc.04/java/lang/invoke/constant/package-summary.html.

This will be useful for tools manipulating the classes and methods.

JEP 340: One AArch64 Port, Not Two
There are two different set of sources, thus ports, targeting ARM 64-bit in the JDK. One is contributed by Oracle, arm64 (hotspot/cpu/arm), and the other is aarch64 (hotspot/cpu/aarch64). This JEP removes arm64, thus all source code used with #ifdefs under hotspot/cpu/arm will be removed and 64-bit ARM build will be default to aarch64. hotspot/cpu/arm will still provide the 32-bit ARM port.

$ cd jdk-src/src/hotspot/cpu/arm
$ hg update jdk-12+1
$ grep -r AARCH64 * | wc -l
1694
$ hg update jdk-12+21
$ grep -r AARCH64 * | wc -l
0
JEP 341: Default CDS Archives
Class Data-Sharing (CDS) is a feature to reduce startup time and benefit from memory sharing. However, if you do not install the JRE with the installer, the CDS archive is not generated by default and java -Xshare:dump has to be run manually.

This can be observed in JDK 11. If you install the JDK 11 GA Release from http://jdk.java.net/11/ , lib/server folder does not contain the CDS archive, classes.jsa file. If you run java -Xshare:dump, it will be generated.

With this JEP, CDS archive will be generated by default.

JEP 344: Abortable Mixed Collections for G1
In order to meet user supplied pause time target, this JEP makes the G1 Garbage Collector abort the garbage collection process, by splitting the set of to-be garbage collected regions (mixed collection set) into mandatory and optional parts, and abort the garbage collection of optional part if pause time target will not be reached otherwise.

JEP 346: Promptly Return Unused Committed Memory from G1
This JEP makes the G1 Garbage Collector return the garbage collected memory areas to the operating system after a period of low application activity. Currently G1 was returning the memory to operating system only after a full GC (or a concurrent cycle) which it avoids, so in effect it was probably not returning the garbage collected memory to operating system at all.

