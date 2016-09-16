# mateplus-poc

__Working effort at a usable MATE+ library__

# License

Note that our LICENSE file is GPL v2. This was not our choice (although it's
not a bad choice). We are using the original license from the two packages
forked/combined for this package:

* Bohnet's "Anna" code (matetools) from
  https://code.google.com/archive/p/mate-tools/source/default/source
* mateplus from https://github.com/microth/mateplus

# Using

This project assumes a Java 8 and Maven build system. Also note that for
running unit tests or executing the models you'll probably need to insure
that the VM has enough memory. If you're using an IDE to run the unit
tests, you probably need to add VM options to the Run Configuration. 

In addition, you'll need our Mavenized MATE model jar (which is over 600MB):

```
<dependency>
    <groupId>edu.memphis.iis</groupId>
    <artifactId>mateplus-models</artifactId>
    <version>4.31</version>
</dependency>
```

Both the jar and the pom.xml file are stored in a private S3 repository so the
normal Maven dependency resolution won't work for you. Luckily, it's not a
public repository due to S3's lack of directory indexing. The files *are*
available via public HTTP access. If you are on a Unix-like system you can use
the bash script `tools/get-models`. You'll need `wget` and `mvn` (Maven)
installed. To run the script...

## Debian/Ubuntu based Linux

If for some reason `wget` and `mvn` aren't already installed:

```
$ sudo apt-get install wget maven
```

To download and install the models into your local Maven repository:

```
$ cd tools
$ ./get-models
```

## Mac

Running the script is the same as above. However, you'll need to install wget
with Homebrew, MacPorts, Fink, or some other technique. we assume that you
have already installed Maven.

## Windows

You can follow the Ubuntu directions above once you have installed the Windows
Ubuntu dev tools. Please see
http://www.howtogeek.com/249966/how-to-install-and-use-the-linux-bash-shell-on-windows-10/
