SuperTMXMerge
=============

SuperTMXMerge is a tool for diffing and merging Translation Memory
eXchange (TMX) files.

Diffing:

![Diff screenshot](https://amake.github.io/SuperTMXMerge/screenshot-diff.png)

3-way merging:

![3-way merge screenshot](https://amake.github.io/SuperTMXMerge/screenshot-merge.png)

Standard text-based diff and merge tools can work with TMX files, but
they will operate on the raw XML text of the file, without
understanding the content. This can result in confusing, unreadable
diffs, or in some cases merges that result in malformed XML.

SuperTMXMerge understands the underlying format and presents diffs and
merges in a (relatively) intuitive way.

**Note:** The "merging" implemented by this tool is known as [3-way
merge]
(http://en.wikipedia.org/wiki/Merge_%28revision_control%29#Three-way_merge).
To simply combine the unique text units of multiple TMXs into a single
TMX, please use the "combine" feature, not the merge feature.

Requirements
------------

You must have Java 1.6 or later installed. To use the launch scripts,
the Java executable must be available on your system PATH. Open a
console or terminal and type `java -version` verify that Java is
installed and configured correctly.

Usage
-----

On Windows run `bin/SuperTMXMerge.bat`. On Linux, etc., run
`bin/SuperTMXMerge`.  On Mac, double-click the SuperTMXMerge
application. This will give you a file selection window. To invoke
various features directly from the command line, use the following.

Diff:

    SuperTMXMerge file1 file2 [-o outputFile]

3-way merge:

    SuperTMXMerge baseFile file1 file2 [outputFile]

Multi-file combine:

    SuperTMXMerge --combine file1 file2 [file3...] [-o outputFile]


Download
--------

Binaries are available at
<https://github.com/amake/SuperTMXMerge/releases>.


License
-------

SuperTMXmerge is distributed under the [GNU Lesser General Public
License, v2.1](http://www.gnu.org/licenses/lgpl-2.1.html) and the
[Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0).

SuperTMXMerge uses the following libraries with the noted licenses:

- Java Diff Utils 1.2.1 (Apache License 2.0)
- Beans Binding 1.2.1 (LGPL-2.1)
- JUnit 4.10 (Eclipse License 1.0)


Source
------

Source is available at <https://github.com/amake/SuperTMXMerge>.


Hints
-----

- TMX metadata is exposed as a tooltip on the filename field in the
  diff and merge windows.

- TU metadata (when present) is exposed as a tooltip on the change or
  conflict item itself (place your cursor on the item's chrome,
  outside of a text field).

- When merging, character-level diff highlighting is shown. The
  differences between the left and center are shown in red and green;
  the right and center are yellow and blue. (Red and yellow are
  deletions; green and blue are insertions.)


Caveats
-------

SuperTMXMerge makes some assumptions about the content of TMX files,
namely:

- Each TU has a "source" TUV with a language that matches the TMX's
  srclang.

- Each TU has a "target" TUV with a language different from the TMX's
  srclang.

Further, when reading TMX files, SuperTMXMerge uniquely identifies
each TU by the serialized content of its source-language &lt;seg> and
the types and contents of its &lt;prop>s. All other TU metadata is
ignored for the purposes of diffing and merging.

If your workflow involves TMX files that don't conform to the above
description, or your workflow relies on distinguishing TUs or TUVs by
their metadata, SuperTMXMerge may not work well for you in its current
form.

If you have a real-world workflow that could be improved by
SuperTMXMerge but is hampered by the above caveats, please raise an
issue on GitHub or get in contact by email.


Building
--------

Build from source with Gradle: `./gradlew assemble`

When building for release, git must be present on the system's path in
order to generate the version number correctly. That means on Windows
you will need something like [msysGit](http://msysgit.github.io/).

Copyright 2013-2016 Aaron Madlon-Kay <aaron@madlon-kay.com>
