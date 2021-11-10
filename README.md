Java source code for an implementation of tri-op redactable blockchains. The code shows the efficiency as well as unit tests for correctness and security.


Further information is explained in the following paper:

> Dousti, M. S., & Küpçü, A. (2021). Tri-op redactable blockchains with block modification, removal, and insertion. *Cryptology ePrint Archive*, [Report 2021/724](https://ia.cr/2021/724).

BibTeX citation:
```
@misc{cryptoeprint:2021:724,
    author       = {Mohammad Sadeq Dousti and
		    Alptekin Küpçü},
    title        = {Tri-op redactable blockchains with block modification, removal, and insertion},
    howpublished = {Cryptology ePrint Archive, Report 2021/724},
    year         = {2021},
    note         = {\url{https://ia.cr/2021/724}},
}
```

# How to run

## Prerequisites
1. JDK 17 or newer
2. Maven 3.8.3 or newer
3. Java compiler, runtime and Maven compiler should be in your PATH. Check with `java -version`, `javac -version` and `mvn --version`.
4. The `JAVA_HOME` environment variable must be set properly.

## Compiling and running tests
Clone the project, and `cd` into project directory. Run the following command `mvn clean verify`. Maven will download project prerequisites, compile the Java classes and package them into a JAR file, and run the tests.

```
[INFO] Scanning for projects...
[INFO]
[INFO] ---------------< io.msdousti:TriOpRedactableBlockchain >----------------
[INFO] Building Tri-Op Redactable Blockchain 1.0-SNAPSHOT
[INFO] --------------------------------[ jar ]---------------------------------
[INFO]
[INFO] --- maven-clean-plugin:2.5:clean (default-clean) @ TriOpRedactableBlockchain ---
[INFO] Deleting /private/tmp/TriOpRedactableBlockchain/target
[INFO]
[INFO] --- maven-resources-plugin:2.6:resources (default-resources) @ TriOpRedactableBlockchain ---
[INFO] Using 'UTF-8' encoding to copy filtered resources.
[INFO] skip non existing resourceDirectory /private/tmp/TriOpRedactableBlockchain/src/main/resources
[INFO]
[INFO] --- maven-compiler-plugin:3.3:compile (default-compile) @ TriOpRedactableBlockchain ---
[INFO] Changes detected - recompiling the module!
[INFO] Compiling 5 source files to /private/tmp/TriOpRedactableBlockchain/target/classes
[INFO]
[INFO] --- maven-resources-plugin:2.6:testResources (default-testResources) @ TriOpRedactableBlockchain ---
[INFO] Using 'UTF-8' encoding to copy filtered resources.
[INFO] skip non existing resourceDirectory /private/tmp/TriOpRedactableBlockchain/src/test/resources
[INFO]
[INFO] --- maven-compiler-plugin:3.3:testCompile (default-testCompile) @ TriOpRedactableBlockchain ---
[INFO] Changes detected - recompiling the module!
[INFO] Compiling 3 source files to /private/tmp/TriOpRedactableBlockchain/target/test-classes
[INFO]
[INFO] --- maven-surefire-plugin:3.0.0-M5:test (default-test) @ TriOpRedactableBlockchain ---
[INFO]
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running io.msdousti.triop.TriOpBlockchainTest
[INFO] Tests run: 7, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.062 s - in io.msdousti.triop.TriOpBlockchainTest
[INFO] Running io.msdousti.triop.BlockTest
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.002 s - in io.msdousti.triop.BlockTest
[INFO] Running io.msdousti.triop.VersionTest
[INFO] Tests run: 10, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.019 s - in io.msdousti.triop.VersionTest
[INFO]
[INFO] Results:
[INFO]
[INFO] Tests run: 18, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO]
[INFO] --- maven-jar-plugin:2.4:jar (default-jar) @ TriOpRedactableBlockchain ---
[INFO] Building jar: /private/tmp/TriOpRedactableBlockchain/target/TriOpRedactableBlockchain-1.0-SNAPSHOT.jar
[INFO]
[INFO] --- maven-shade-plugin:3.2.4:shade (default) @ TriOpRedactableBlockchain ---
[INFO] Including org.openjdk.jmh:jmh-core:jar:1.33 in the shaded jar.
[INFO] Including net.sf.jopt-simple:jopt-simple:jar:4.6 in the shaded jar.
[INFO] Including org.apache.commons:commons-math3:jar:3.2 in the shaded jar.
[INFO] Including org.openjdk.jmh:jmh-generator-annprocess:jar:1.33 in the shaded jar.
[INFO] Including org.projectlombok:lombok:jar:1.18.22 in the shaded jar.
[INFO] Replacing /private/tmp/TriOpRedactableBlockchain/target/benchmarks.jar with /private/tmp/TriOpRedactableBlockchain/target/TriOpRedactableBlockchain-1.0-SNAPSHOT-shaded.jar
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  2.650 s
[INFO] Finished at: 2021-11-10T07:38:37+01:00
[INFO] ------------------------------------------------------------------------
```

You should see a `BUILD SUCCESS` at the end. You can also notice that 18 tests were executed, and all of them were successful. The project tests consist of functionality and security tests, and can be found under the `./src/test` directory.

## Running benchmarks
This project uses [Java Microbenchmark Harness (JMH) framework](https://github.com/openjdk/jmh) to run the benchmarks, which are located in [`src/main/java/io/msdousti/benchmark/AllBenchmarks.java`](https://github.com/msdousti/TriOpRedactableBlockchain/blob/master/src/main/java/io/msdousti/benchmark/AllBenchmarks.java). The benchmark setup is as follows:

```java
@State(Scope.Benchmark)
@Fork(value = 2, warmups = 1)
@Warmup(iterations = 5, time = 4000, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, time = 4000, timeUnit = TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.Throughput)
```

It means the benchmark will create two forks, out of which 1 is a warmup fork. Each fork has five warmup iterations and five normal iterations. Each iteration will take 4000 milliseconds. The benchmark mode is set to throughput. All in all, each benchmark will take 2 minutes (for a total 16 minutes for 8 benchmarks). The warmups are there beacuase JVM optimizes code on the go.

To run the benchmarks, simply execute `java -jar target/benchmarks.jar` in the project directory. After around 16 minutes, it will output a nices table for the benchmakrs. The results below are generated on an Apple 2020 laptop with M1 chip and 16 GB of memory.

```
Benchmark                     Mode  Cnt     Score    Error  Units
AllBenchmarks.installChange  thrpt   10  1031.999 ±  7.846  ops/s
AllBenchmarks.installInsert  thrpt   10  1011.839 ±  4.798  ops/s
AllBenchmarks.installRemove  thrpt   10  1023.124 ±  4.398  ops/s
AllBenchmarks.redactChange   thrpt   10  1984.024 ±  7.232  ops/s
AllBenchmarks.redactInsert   thrpt   10  1976.893 ± 20.512  ops/s
AllBenchmarks.redactRemove   thrpt   10  1958.584 ± 68.914  ops/s
AllBenchmarks.sigSign        thrpt   10  1993.868 ± 62.613  ops/s
AllBenchmarks.sigVerify      thrpt   10  2154.005 ± 66.021  ops/s
```
