# Batch import samples

This tool allows you to import samples from an input folder.


There are two modes:

1. Samples are grouped by treatments
2. Samples are not grouped

## Grouped by treatment

Sample folders are located in sub-folders that represent the treatments.

```
input folder
├── treatment 1
│   ├── sample 1
│   │   ├── file 1
│   │   └── file 2
│   └── sample 2
│       ├── file 1
│       └── file 2
├── treatment 2
│   └── ...
└── ...
```

## Ungrouped samples

The input folder directly contains the sample folders.

```
input folder
├── sample 1
│   ├── file 1
│   └── file 2
└── sample 2
    ├── file 1
    └── file 2
```
