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

Graphical interface: Launch SuperTMXMerge.jar as usual:

    java -jar SuperTMXMerge.jar

Command-line diff:

    java -jar SuperTMXMerge.jar file1 file2

Command-line 3-way merge:

    java -jar SuperTMXMerge.jar baseFile file1 file2

Command-line 3-way merge (for use with VCS tools):

    java -jar SuperTMXMerge.jar baseFile file1 file2 outputFile


License
=======

SuperTMXmerge is distributed under the GNU General Public License v2:

http://www.gnu.org/licenses/gpl-2.0.html


Source
======

Source is available at:
https://github.com/amake/SuperTMXMerge


Copyright 2013 Aaron Madlon-Kay <aaron@madlon-kay.com>
