SuperTMXMerge
=============

SuperTMXMerge is a tool for diffing and merging Translation Memory 
eXchange (TMX) files. 

Standard text-based diff and merge tools can work with TMX files, but 
they will operate on the raw XML text of the file, without understanding 
the content. This can result in confusing, unreadable diffs, or in some 
cases merges that result in malformed XML. 

SuperTMXMerge understands the underlying format and presents diffs and 
merges in a (relatively) intuitive way. 


Usage
=====

On Windows run `SuperTMXMerge.bat`. On Linux, OS X, etc. run
`SuperTMXMerge.sh`. This will give you a file selection window.

File selection window from command line:

    java -jar SuperTMXMerge.jar

Command-line diff:

    java -jar SuperTMXMerge.jar file1 file2

Command-line 3-way merge:

    java -jar SuperTMXMerge.jar baseFile file1 file2

Command-line 3-way merge (for use with VCS tools):

    java -jar SuperTMXMerge.jar baseFile file1 file2 outputFile


Download
========

Binaries are available at https://github.com/amake/SuperTMXMerge/releases


License
=======

SuperTMXmerge is distributed under the GNU Lesser General Public License 
v2.1:

http://www.gnu.org/licenses/lgpl-2.1.html


Source
======

Source is available at:
https://github.com/amake/SuperTMXMerge


Caveats
=======

SuperTMXMerge makes some assumptions about the content of TMX files, namely:
- Each TU has a "source" TUV such that the TUV's (xml:)lang is equalsIgnoreCase
  to the TMX's srclang.
- Each TU has a "target" TUV where the TUV's (xml:)lang is !equalsIgnoreCase to
  the TMX's srclang.

Further, when reading TMX files, SuperTMXMerge uniquely identifies each TU by
the serialized content of its source-language &lt;seg> and the types and
contents of its &lt;prop>s. All other TU metadata is ignored for the purposes
of diffing and merging.

If your workflow involves TMX files that don't conform to the above description,
or your workflow relies on distinguishing TUs or TUVs by their metadata,
SuperTMXMerge may not work well for you in its current form.

If you have a real-world workflow that could be improved by SuperTMXMerge but is
hampered by the above caveats, please contact me.


Notes
=====

When building for release, git must be present on the system's
path in order to generate the version number correctly. That means
on Windows you will need something like msysGit:
http://msysgit.github.io/

Copyright 2013 Aaron Madlon-Kay <aaron@madlon-kay.com>
