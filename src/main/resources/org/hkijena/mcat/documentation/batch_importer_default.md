# Batch import samples

This tool allows you to import samples from an input folder in batch mode.

There are two possibilities for the input folder structure:

1. Samples are grouped by treatments.
2. Samples are not grouped.

### Grouped by treatment

Sample folders are located in sub-folders that represent different treatments.

```
input folder
├── treatment 1
│   ├── sample 1
│   │   ├── file 1 (e.g. raw image)
│   │   └── file 2 (e.g. ROI file)
│   └── sample 2
│       ├── file 1
│       └── file 2
├── treatment 2
│   └── ...
└── ...
```

### Not grouped by treatment

The input folder contains sample folders directly.

```
input folder
├── sample 1
│   ├── file 1 (e.g. raw image)
│   └── file 2 (e.g. ROI file)
└── sample 2
    ├── file 1
    └── file 2
```

### Import options

_Subfolders are treatments_
- If this option is checked, subfolders are interpreted as individual treatments.
	
_Include treatment in name_
- If this option is checked, the sample name will be headed by the respective treatment name
	
_Import raw images_
- Check this option to automatically import raw images.
	
_Raw images pattern_
- With this regular expression pattern raw images can be filtered by their name. See table at the bottom for more information about regular expressions.
	
_Import ROI_
- Check this option to automatically import ROI files.

_ROI pattern_
- With this regular expression pattern ROI files can be filtered by their name.
	
### Regular expressions for automated file import

Raw image files and ROI files can be imported automatically by specifying regular expressions for file name pattern matching.
The example `.*LinReg.*\.tif` finds all .tif files that include the pattern `LinReg`. The regular expression `.*` means that there can be any character sequence before and after `LinReg`. The `.` character of the file extension has to be escaped by `\`. General rules for using regular expressions for pattern matching are listed in the following table:  
	
| Regular expression   | Description                                                         |
| -------------------- | ------------------------------------------------------------------- |
| `.`                 | Matches any character.                                              |
| `^regex `         | Finds regex that must be at the beginning of the file name.         |
| `regex$`          | Finds regex that must be at the end of the file name.               | 
| `[abc]`         | Matches a or b or c.                                                  |
| `[abc][de]` | Matches a or b or c followed by either d or e.                           |
| `[^abc]`         | Matches any pattern except a, b and c.                               |
| `[a-c1-5]`      | Matches a letter in the range a to c and digits in the range 1 to 5.  |
| `a|b`             | Matches a or b.                                                      |
| `ab`              | Matches a directly followed by b.                                    |
| <hr/>              | <hr/>                                                                |
| `\d`              | Matches any digit (short for \[0-9\].                                |
| `\D`              | Matches any non-digit (short for \[^0-9\].                           |
| `\s`              | Matches whitespace characters (e.g. tab, line break).                |
| `\S`              | Matches any non-whitespace character.                                |
| `\w`              | Matches any word character (short for \[a-zA-Z0-9\]).                |
| `\W`              | Matches any non-word character.                                      |
| `\b`              | Matches word boundaries.                                             |
|<hr/>               | <hr/>                                                                |
| `*`               | Occurs zero or more times (e.g. `.*` finds any character sequence). |
| `+`               | Occurs one or more times.                                            |
| `?`               | Occurs zero or one times.                                            |
| `{X}`             | Occurs X number of times.                                            |
| `{X,Y}`          | Occurs between X and Y times.                                         |

For more information on regular expressions and pattern matching checkout [this online documentation](https://www.oracle.com/technical-resources/articles/java/regex.html "Oracle Java RegEx").

