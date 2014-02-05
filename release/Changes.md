SuperTMXMerge change history
============================

# 1.0.1
- Fix Japanese localization

# 1.0
- Updated Japanese localization
- Fixed licensing issues
- Under-the-hood changes for embedded use
- Build system fixes

# 0.9.5
- Added Mac-specific package
- Added menu bar
- Fixed mouse wheel scrolling
- Made diff, merge windows modal for embedded use
- Removed build dependency on NetBeans

# 0.9.4
- Added ability to do "baseless" two-way merges
- Added ability to batch-combine many TMX files
- Fixed output of TUs with complex content
- Fixed duplicated TUs in merge output in some situations
- Other small fixes and improvements

# 0.9.3
- Added ability to save diffs as TMX files
- Exposed full input TMX file path in metadata tooltip (hover cursor over
  TMX name in diff, merge windows)
- Fixed problem where new, identical TUs added to both file1 and file2 would
  be left out of merged file

# 0.9.2
- Fixed failure to extract complex JAXBTuv content, reported by Chase Tingley
- Fixed failure to handle TUs missing source/target TUVs, reported by Paul
  Muraille
- Added caveats, hints sections to readme

# 0.9.1
- Updated Japanese localization
- Fixed merging issue reported by Paul Muraille

# 0.9.0
- Initial release
