# libpgn
The PGN (Portable Game Notation) parser.

This project is an attempt to learn something new. Since I was always interested in working with the text, I tried to write a parser for PGN files. I was wondering what difficulties might arise during the design and implementation of the parser.
The parser basically follows the specification but has a flexible configuration that allows you to deviate or vice versa to strictly follow the PGN specification.  I had to balance between fine writing code and performance.


# Large files splitting

Here is example using [try-with-resources](https://docs.oracle.com/javase/7/docs/technotes/guides/language/try-with-resources.html)

```java
File file = new File("/home/user/pgn/5_gb_file.pgn");

long chunkSize = 1_000_000 * 1000; // 1GB
int rwBufferSize = 8192 * 2; // The amount of bytes read/write at once.

try (PgnFileSlicer slicer = new PgnFileSlicer(file, new SimpleFileNamingStrategy(), rwBufferSize, chunkSize)) {
    slicer.write();    
}
```

The `PgnFileSlicer` implements the `java.io.Closeable` so the caller should always perform `PgnFileSlicer#close()` to free underlying resources.

#### Output file naming

There is a simple way to customize newly created files names. We should implement the `FileNamingStrategy` and pass it to `PgnFileSlicer` as constructor argument.
There is a default implementation for that interface that appends to file name the chunk number. Using this we can easily change output files directory.