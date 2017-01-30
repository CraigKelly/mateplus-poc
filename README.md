# mateplus-poc

This is more of a demo and a wrapper around MATE+.

If you are looking at this, you are probably interested in the file
`sample/deps.sh`

# License

All code in the repository is released under the MIT license,

However, the two main libraries in use are licensed under the GPL v2:

* Bohnet's "Anna" code (matetools) from
  https://code.google.com/archive/p/mate-tools/source/default/source
* mateplus from https://github.com/microth/mateplus

# Dependencies

You should have Java 8, a recent version of Maven (`mvn`), and `wget` (for the
scripts that download dependencies).

All scripts should run on a system with a recent-ish bash shell. (Note that you
can now get an Ubuntu core with bash running on Windows 10. :)

Also note that for running unit tests or executing the models you'll probably
need to insure that the JVM has enough memory. If you're using an IDE to run
the unit tests, you probably need to add VM options to the Run Configuration.

Before running this sample project, you need to make sure that you have
installed the models and libraries you need. You can do this by running
`script/deps.sh`. It downloads and installs the necessary JAR's into the local
Maven repository. Note that this includes a MATE model jar that clocks in at
over 600MB.

# The Models

The original project used to build the model jar is in the sub-directory
`mateplus-models`. It is there for documentation purposes - you shouldn't need
to build or run it.
