# MSOT Cluster Analysis Toolkit (Mcat)
<!---[![DOI](https://zenodo.org/badge/290702041.svg)](https://zenodo.org/badge/latestdoi/290702041)--->

## Copyright

Copyright by Dr. Bianca Hoffmann, Ruman Gerst, Dr. Zoltán Cseresnyés and Prof. Dr. Marc Thilo Figge

Research Group Applied Systems Biology - Head: Prof. Dr. Marc Thilo Figge  
[https://www.leibniz-hki.de/en/applied-systems-biology.html](https://www.leibniz-hki.de/en/applied-systems-biology.html)  
HKI-Center for Systems Biology of Infection  
Leibniz Institute for Natural Product Research and Infection Biology - Hans Knöll Insitute (HKI)  
Adolf-Reichwein-Straße 23, 07745 Jena, Germany    

The project code is licensed under BSD 2-Clause.
See the LICENSE file provided with the code for the full license.

## System Requirements  
*Mcat* is Java-based and generally platform-independent. It was tested on Windows 7, Linux Ubuntu 20.10 and macOS 10.12.6.

## Installation  
If you don’t have ImageJ installed yet, you can get it [here](https://imagej.net/Fiji). To integrate *Mcat* into your ImageJ distribution you can either install it via its ImageJ Update Site or manually.  
  
Installation via Update Site:
- In ImageJ go to *Help > Update...*
- Click *Manage Update Sites*.
- Scroll down the list and tick the check box for *Mcat*.
- Close the Manage Update Sites window.
- Click *Apply Changes*.
- Restart ImageJ when prompted so.
- *Mcat* should now appear at the bottom of the Plugins menu.  
  
Manual installation:
- Download the Mcat_Plugin.zip package (under Releases on the right hand side) and unzip the \*.zip archive.
- Copy all files from the unzipped folder (Mcat-1.0.0.jar and dependencies folder) into the plugins directory of your ImageJ installation. 
- Restart ImageJ.
- *Mcat* should now appear at the bottom of the Plugins menu.

## Demo
See User Manual for instructions on how to run *Mcat* and the expected output data.


## Running MCAT from command line

You can run an MCAT project from command line:

```
./ImageJ-linux64 --pass-classpath --full-classpath --main-class org.hkijena.mcat.Main --project-file=<Project file> --output-path=<Output path>
```

## Credits

### Breeze icons

Our plugin uses the [Breeze icon pack](https://github.com/KDE/breeze-icons) by KDE,
licensed under [lesser GPL](https://raw.githubusercontent.com/KDE/breeze-icons/master/COPYING.LIB).

### Font awesome icons

Our plugin uses icons from [Font Awesome](https://fontawesome.com/) by Fonticons, Inc.
We obtained the icons via the [Font-Awesome-SVG-PNG project](https://github.com/encharm/Font-Awesome-SVG-PNG) by Code Charm.
The Font Awesome font is licensed under the [SIL OFL 1.1](http://scripts.sil.org/OFL).
Font-Awesome-SVG-PNG is licensed under [MIT](https://raw.githubusercontent.com/encharm/Font-Awesome-SVG-PNG/master/LICENSE).

