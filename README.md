# Briss 2.0 
![Java CI with Gradle](https://github.com/mbaeuerle/Briss-2.0/workflows/Java%20CI%20with%20Gradle/badge.svg?branch=master)

Briss is a small application to crop PDF files. It is useful for example to crop whitespaces at the edeges so text is rendered bigger on small screens like eInk displays or tablet PCs.

It helps the user to decide what should be cropped by creating an overlay of similar pages, e.g. all pages within a PDF
 having the same size or orientation. Even and odd pages are separated too.

Version 2.0 is intended to be a GUI Update for the Briss PDF cropping tool.
It is based on Briss 0.9 which is located at sourceforge: http://sourceforge.net/projects/briss/

Briss is running on Windows, MacOS and Linux and works with Java 8 and above.

## Installation

Currently Briss 2.0 is in alpha therefore some features are still missing (for example the page skip list).
If you want to give it a try you can download the pre release from https://github.com/mbaeuerle/Briss-2.0/releases.
There is a version of Briss for Java 8 and one version for any newer version of Java.

## Usage
You can run the application by executing the following command in terminal (in Windows use `Briss-2.0.bat`):

```
./bin/Briss-2.0
```
or
```
./bin/Briss-2.0 cropthis.pdf
```

## Commandline

If you prefer command line and trust the basic automatic detection algorithm
use it this way:

```
./bin/Briss-2.0 -s [SOURCEFILE] [-d [DESTINATIONFILE]]
```
Example:
```
./bin/Briss-2.0 -s dogeatdog.pdf -d dogcrop.pdf
./bin/Briss-2.0 -s dogeatdog.pdf
```
the second line will create the cropped pdf into `dogeatdog_cropped.pdf`

To split according to columns/rows, respectively use the `--split-col` and `--split-row` arguments. For example:
```
./bin/Briss-2.0 -s dogeatdog.pdf -d dogcrop.pdf --split-col
```

Splitting columns will try to split the pdf into two columns. Splitting rows will try to split the pdf into two parts
by cutting pages in half.

## Images

Startscreen with drag and drop support:
![Image of BRISS 2.0 Startscreen](img/startScreen.png)
Cropping view:
![Image of BRISS 2.0 Cropping View](img/croppingView.png)

## Improvements done in Briss 2.0
- Small refinements on gui which improve the workflow
- Better file chooser than provided by swing
- Added support for drag and drop
- Fixed an issue with the image preview showing nothing when all pages look mostly the same

## Build instructions

### Prerequisites
Make sure you have JDK 11 or later installed.

### Build
To build, run the following command:

```
./gradlew distZip
```

You can find the built version in `build/distributions`

## Libraries used
 * This software uses two libraries to render and crop PDF files: 
  * itext (AGPLv3) http://itextpdf.com/ 
  * jpedal (LGPL) http://www.jpedal.org/
  * JavaFX (GPLv2) https://openjfx.io
  
