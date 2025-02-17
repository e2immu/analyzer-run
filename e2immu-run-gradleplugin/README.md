Gradle plugin
=============

The primary goal of the gradle plugin is to provide the inspector and analyzer with a proper classpath,
and a set of source directories.
These cannot be set as plugin options.


Input sources
-------------

- `sourcePackages` (list of strings):
    - which source packages to analyze.
    - If absent, all sources are analyzed.
    - When an entry ends in a dot, all sub-packages are included.

- `testSourcePackages` (list of strings):
    - Similar to `sourcePackages`. The difference is made only for some analyses.

- `analyzedSourcesDir` (single string):
    - where to write the results of analysis of sources
    - if absent, do not write out result of analysis
- `analyzedTestSourcesDir` (single string):
    - similar to `analyzedSourcesDir`

- `jmods` (list of strings):
    - which Java modules to add to the classpath
    - when absent, the default modules are added. This is a very short list.

Annotated APIs
--------------

- `analyzedAnnotatedApiDirs` (list of strings):
    - When absent, analyzed annotated APIs are read from the default location.
    - When present, they are read from the locations specified here.
    - When the first entry in this list starts with '+', the default locations are kept.
    - When present and empty, no analyzed annotated APIs are read.

- `annotatedApiSourcePackages` (list of strings):
    - When included, treat part of the analyzer's input as annotated API sources to be processed.
    - Each entry is a package name. Sources in this package will be processed.
    - When an entry ends in a dot, all sub-packages are included as well.

- `analyzedAnnotatedApiTargetDirectory` (single string):
    - Only relevant when `annotatedApiSourcePackages` is present.
    - Absent means that the analyzed versions are not written out.
    - Present defines where they will be written to.

- `annotatedApiTargetDirectory` (single string):
    - When present, write AAPI skeletons to this directory for all active source packages.
    - Merge strategy: append!

- `decompiledClassesDir` (single string)
    - location of the decompiled classes of JDK, libs
    - when absent, de-compilation does not take place, and the analyzer uses the shallow analyzer rather than the real
      analyzer.

Analysis
--------

- `incrementalAnalysis` (boolean): when `true`, build on all information already present in `analyzed(Test)SourcesDirs`
- `analysisSteps` (list of strings):
    - describes which analysis steps to carry out.
    - If absent, all steps are executed.

General options
---------------

- `sourceEncoding` (single string): alternative source encoding, default is UTF-8
- `jre` (single string): alternative JRE location

Use cases 202502
----------------

At this moment, we're mostly concerned with running _e2immu_ analysis on a large code base, with as little human
interaction as possible. We take the point of view of a refactoring tool that uses _e2immu_, rather than of a developer
who wants _e2immu_ to guide their development.

The refactoring tool will only make use of the Analyzed AAPI files (`.json` files stored in the
`analyzedAnnotatedApiDirs`). How it runs is of no concern here.

All of _e2immu_'s efforts should run as a single "update" step that

1. reads in AAPI files
2. parses all sources, and all sources of statically linked dependencies, where the analysis has not been confirmed, or
   the sources have changed
3. generates updated AAAPI files
4. (over)writes AAPI files in correct format to highlight human changes compared to the computation or defaults
4. writes a detailed change-log report for the user

Important here is what constitutes "source", "lib", and "JDK".
The classic situation has sources, libs, and jmods.
But once the jars have been expanded to source code, we could easily first decide to analyze
one library as "source", the rest of the libs as libs, the jmods as jmods.
The reason for this is limiting the dependency graph: for abstract methods, the analyzer will search the entire code
base for implementations to derive a value. The "intention" can be made clear by limiting the available implementations.

Note that there is a strict dependency among jmods, so we could analyze `java.sql` in function of its 4 dependent jmods
`java.lang`, `java.logging`, `java.xml` and `java.transaction.xa`.

If the sources of a given package have not changed (commit hash, jar hash), then/and AAAPI-files exist,
(and) we can load those. We can compare the loaded properties to those in the current AAPI file.
Changes will be recorded. A new AAAPI file will be written out, and the AAPI file will be updated to acknowledge the
changes. This cycle can be repeated, which allows the user to manually override the computed properties.
If the AAPI file did not yet exist, we keep the AAAPI as is, and write out the AAPI skeleton based on the computed
values.

If, for a given package, sources have changed or no AAAPI-files exist yet, we should run the full analyzer on the
sources of
this package, given the current scope of (source, lib, jmod). While the result may not be optimal, an AAAPI file can be
written out, as can an AAPI skeleton.
The shallow analyzer will only be used for interfaces or abstract methods which have no implementation in the current
sources.

### Algorithm in detail

1. Determine sources, libraries, jmods; create JavaInspector instance from the InputConfiguration. This catalogues
   sources and compiled classes, and provides a way of accessing them. Each part of the path has an associated hash: for a jar, it'll be the hash of the jar; for a directory of source files, a hash of the combined sources; etc.
3. Determine which of sources, libs, jmods must be parsed from source, and then analyzed. The criterion must be
    1. their sources have changed (the AAAPI file has been computed for a different source hash)
    2. or, at least one AAAPI file is missing for any of the packages in the group
4. For each of the packages not to be analyzed from sources: load the AAPI file if present, and store its overrides.
   Ensure that the AAAPI is loaded afterwards, keeping the overrides in place. If not present, it will be loaded in the
   background during source code parsing. Ensure that the AAAPI is loaded as well each time a type from a given package
   is loaded by the byte code interpreter.
5. Ensure that source code is generated or present for all libraries and jmods that need parsing from source.
5. Run the inspector on all the sources, do background work as described.
6. For all the packages in the sources with an AAPI file, load that file, store the overrides, and apply them. Do
   background work as described.
6. Determine the packages in libs/jmods from source that are statically reachable from the sources. We'll restrict
   analysis to those.
7. Run prep analyzer on the combined reachable sources.
8. Run modification analyzer in the order produced by the prep analyzer.
6. Write out AAAPI files for all packages that were analyzed from sources. Every package in the InputConfiguration,
   statically reachable from the sources, must now have a current AAAPI file. This should ensure that if we re-run the
   algorithm without changing any sources, no work is done.
7. Write out AAPI files (typically, overwrite them), taking care to
    1. keep all the overrides and "confirmed"s, so that manual work is never lost
    3. add usage information (on methods, types) so that there is a priority list for review





