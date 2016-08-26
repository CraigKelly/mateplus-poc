# mateplus-poc

Working effort at a usable MATE+ library

Note that our LICENSE file is GPL v2. This was not our choice (although it's
not a bad choice). We are using the original license from the two packages
forked/combined for this package:

* Bohnet's "Anna" code (matetools)
* mateplus from https://github.com/microth/mateplus

You should be able to build with Maven, *but* you'll need our Mavenized MATE
model jar:

```
<dependency>
    <groupId>edu.memphis.iis</groupId>
    <artifactId>mateplus-models</artifactId>
    <version>4.31</version>
</dependency>
```

Since that is available from a private Maven repository (we're working on
making that public), please contact Craig for support.
