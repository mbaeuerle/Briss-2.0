<!---
 * Copyright 2010 Gerhard Aigner
 * 
 * This file is part of BRISS.
 * 
 * BRISS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * BRISS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * BRISS. If not, see http://www.gnu.org/licenses/.
--> 

# Change Log - BRISS
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/)
and this project adheres to [Semantic Versioning](http://semver.org/).

## [Unreleased]

### Added

* Resize handles on all corners and edges
* Mouse cursors resembling the actions which can be taken
* Deselect all crop rectangles on ESC

### Changed
* [Issue-41](https://github.com/mbaeuerle/Briss-2.0/issues/41) Reverse crop rectangle selection order. Newest rectangle under cursor is selected.
* Update to Gradle 7.4
* Select crop rectangles with single click. Select multiple rectangles with `<shift>+click`.

## [2.0-alpha 3] - 2020-05-26

### Added
* Fallback overlay image algorithm to improve overlay when most pages look the same

### Changed
* Update to Gradle 6.3

## [2.0-alpha 2] - 2020-04-15

### Changed
* Code internal changes
* Java 8 now required because of JavaFX

## [2.0-alpha] - 2018-08-06

### Added
* Default size and position of window on startup
* Drag and drop support for loading PDFs
* Split crop rectangles horizontally or vertically
* Native file chooser for loading and storing PDFs

### Changed
* Increase progress bar update interval
* Improve visuals and arrangement of GUI
* Use Gradle as build tool

### Removed
* Popup to exclude pages after loading PDF
* Some crop functions like maximizing rectangles

## [0.9] - 2012-05-26
 * better handling of bookmarks
* library updates
* Rastislav Wartiak added a couple of user-interface/rectangle handling features
** rectangles have setable size and position
** keyboard bindings for actions
** show rectangle size of selected rectangles

## [0.0.13] - 1-20-4 
* Added automatically crop rectangle calculation
* Updated libraries (itext, jpedal) fixing several problems
* JP2000 support, finally!
* Added hotcorners(upper left and lower right) to resize existing crop rectangles
* Providing a single file as argument now directly loads it on startup of Briss 
     (can be handy if you have a shortcut defined like "open with")
* Command line cropping (at the moment just with automatic crop algorithm,
     see Readme for instructions)
* Better visualization of merged images
* Added possibility to reload pdfs for using other excluded pages while
     retaining crop rectangles
* Added drag&drop functionality
* Automatically clipping of crop rectangles to visible area
* Copy/paste of crop rectangles between clusters 	
* Hopefully fixed bug where on Mac the crop rectangles couldn't be deleted

## [0.0.12] - 2010-01-11
* Added Icon (thx to joão ziliotto)
* Reworked the GUI
* updated iText and JPedal libraries
* included a preview button

## [0.0.11] - 2010-10-10
* Exclude pages from merging
* Merged blocks are now sorted according to their lowest page number
* Faster scrolling (mouse)
* Fixed an issue where huge pages would cause BRISS to crash

## [0.0.10] - 2010-10-01
* fixed issure where a trim or bleed box would influence the cropping behaviour
* smaller file size through newer libraries
  * itext
  * jpedal 

## [0.0.9] - 2010-06-08
* Maximal pageheight of preview image is set to 600pixel
* last directory is being remembered 

## [0.0.8] - 2010-03-17
* Fixed a bug where bookmarks got lost during crop-transformation

## [0.0.7] - 2010-03-15
* Meta information is copied to the cropped file
* Small visual fix in progress bar
* Ghost rectangle shows the size of the last drawn crop rectangle (for people who want to have equally sized pages)
* Dragging of crop rectangles (press and hold the mouse button inside a crop rectangle)
* Single removal of crop rectangles by pressing the right mouse button inside the rectangle to be deleted
* Selected crop rectangles (=>ctrl+left mouse click into the rectangle) can be sized to the maximum of all selected widths/heights.

## [0.0.6] - 2010-05-13
* Added multiple column support (actually it's a multiple rectangle capability)
* Integrated a small description how to use the software.

## [0.0.5] - 2010-05-12
* Clustering of pages is now done with a small buffer regarding the size in order not to create to much clusters.
* Fixed a bug for pages not having a crop box... 

## [0.0.4] - 2010-05-11
* Switched to JPedal for Image rendering since its more robust

## [0.0.3] - 2010-05-11
* Resolved Bug:
* Crop rectangle height was limited to the same value as the width... 

## [0.0.2] - 2010-05-10
* Performance improvements
* Resolved Bugs:
   #2999011 - Handling of rotated PDFs is implemented
   #2999012 - Cropped rectangle is now limited to the preview image size 

## [0.0.1] - 2010-05-09
* Initial release
