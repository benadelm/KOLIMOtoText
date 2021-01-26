# KOLIMOtoText

A tool used in the research project [hermA](https://www.herma.uni-hamburg.de/en.html) for extracting the document text of certain TEI and XHTML files in the [KOLIMO](https://kolimo.uni-goettingen.de/index.html) corpus.

The program batch processes all files in a directory, parsing the XML and converting it to plain text taking into account the formatting implications of some TEI and (X)HTML elements as well as a number of mark-up schemes (mainly CSS classes) specific to the TEI and XHTML documents collected in KOLIMO. (See *special treatment of tags* below for a description of how different tags are treated.)

# Installation

The software has been tested with Windows 10 and Linux. Being written in Java, it should run on any platform Java supports; you will need a Java runtime to run the software. It has been developed and tested with Java 8, but newer versions may also work.

# Input

The program expects three command-line arguments:

1. path to the input directory
2. path to the output directory
3. the conversion type (`tools` or `human`)

All files in the input directory are processed. For every file, the result of the text extraction is written to a file with the same name in the output directory. The extension of the file is not changed, so if the name of the input file is `name.xml`, the output file will also have the name `name.xml` although it is plain text and not XML. You may specify the same directory as input and output directory, but be aware that the input files will be overwritten then.

The tool can only process XML files whose root element is `TEI` or `html`. If another root element is encountered in any file in the input directory, the tool will exit with an error. The same will happen if any file in the input directory is not valid XML (invalid characters, unclosed tags etc.) or if an IO error (e.g. a file cannot be opened) occurs. Note that in contrast to XHTML files, HTML files may not be valid XML as in HTML certain tags (such as `<br>`) are not required to be closed.

The encoding of the plain text output files will be UTF-8 regardless of the encoding given in the XML declaration (`<?xml version="1.0" encoding="..."?>`).

The tool supports two types of conversion:

* `tools` creates an output with only the text content (hopefully suited for further processing by automatic tools).
* `human` creates an output that is a bit more useful for humans. The main difference is that the absence of images or other non-textual material is indicated by a placeholder string (such as <span lang="de">`[Bild]`</span>).

For example, with the command line options

	kolimo_dir output_dir tools

the files in the directory `kolimo_dir` are read and converted in `tools` mode and the extracted text is saved to files in the directory `output_dir`.

# Conversion Logic

In principle, the tool just concatenates the text content of all XML elements in the input. However, some elements receive special treatment because of certain semantics that cannot be expressed in plain text or only with more sophisticated formatting than just concatenation of the inner text. Furthermore, some issues with the document text itself are addressed by correction heuristics.

Line breaks in TEI, either in the text itself or denoted by some tag (such as `<lb/>`), are generally preserved, but unnecessary white space is removed so that there are never two or more consecutive empty lines, no empty lines at the beginning or end of the output file, and no white space at the beginning or end of lines. Spaces inside lines are collapsed, too. The same applies to XHTML, except that line break characters in the text are treated like spaces (tags like `<br/>` still lead to the insertion of line breaks, see below).

The special treatment of TEI and XHTML tags and even more the text normalization heuristics are highly tailored to the data for which this program has been created: literary texts written between 1870 and 1920 from the KOLIMO corpus.

## Special Treatment of Tags

Some tags are skipped entirely; in TEI these are:

* `teiHeader` (structured metadata)
* `front` (front pages, book covers and the like, not part of the main text)
* `back` (similar to `front`)
* `date`
* `sic` (usually appears together with `corr`, with `sic` containing incorrect or inaccurate text and `corr` the correction; the latter is preferred)
* `fw` (running decoration such as headers or footers, not part of the main text)
* `ptr` (defined to have no content anyway)
* `milestone` (defined to have no content anyway)
* `title`

In XHTML only the `head` tag is skipped.

Other TEI tags with special treatment are:

* `space` is treated like a space character
* `lb` indicates a line break and normally leads to a line break in the output (see *hyphenation heuristic* below for exceptions)
* `pb` indicates a page break and is treated like a line break
* the contents of `l`, `row` and `item` are surrounded by line breaks
* if a `div` element has a `type` attribute with value `contents` (`<div type="contents">`), it contains a table of contents and is skipped; otherwise, it is treated like `p`
* `p` indicates a paragraph and its content is surrounded by one empty line before and after
* `list`, `dateline`, `postscript`, `salute`, `table` and `head` are treated like `p`
* the contents of `cell` are preceded by a tabulation (U+0009)
* if a `note` element has a `place` attribute with value `foot`, it is a footnote and if the conversion type is `human`, its content is surrounded by <span lang="de">`[Fußnote: ...]`</span>; otherwise it does not receive any special treatment (see *known problems* below)
* `gap` indicates material left out and can only contain metadata; its contents are skipped, but in `human` conversion mode, `[…]` is inserted to indicate the omission
* `figure` and `graphic` usually contain images and cannot be represented in plain text; their contents are skipped, but in `human` conversion mode, <span lang="de">`[Bild]`</span> is inserted as a placeholder
* for similar reasons, the contents of `formula` are skipped; in `human` conversion mode, <span lang="de">`[Formel]`</span> is inserted as a placeholder

The XHTML tags with special treatment are:

* generally, the contents of HTML block elements (`div`, `p`, `ol`, `ul`, `blockquote`, `h1` to `h6`) are surrounded by one empty line before and after
* `br` indicates a line break and normally leads to a line break in the output (see *hyphenation heuristic* below for exceptions)
* a line break is also inserted before the contents of `tr` (table row)
* `img` indicates an image and cannot be represented in plain text; its contents are skipped, but in `human` conversion mode, <span lang="de">`[Bild]`</span> is inserted as a placeholder
* if an `a` element has a `class` attribute with value `pageref` (`<a class="pageref">`), it contains a page number and is skipped; otherwise, no special processing takes place
* if a `div` or `table` element has a `class` attribute with value `toc` (`<div class="toc">`, `<table class="toc">`), it contains a table of contents and is skipped; otherwise, it is treated like any other block element
* if a `span` element has a `class` attribute with value `footnote` (`<span class="footnote">`), it is a footnote and if the conversion type is `human`, its content is surrounded by <span lang="de">`[Fußnote: ...]`</span>; otherwise it does not receive any special treatment (see *known problems* below)
* the contents of `td` are preceded by a tabulation (U+0009)
* `hr` leads to the insertion of an empty line
* the contents of `li` are surrounded by line breaks

## Normalization

### Character Replacement

Many TEI documents in the KOLIMO corpus contain a ‘long s’ (ſ, U+017F LATIN SMALL LETTER LONG S). This character is replaced with a normal ASCII s (both for TEI and XHTML inputs). Additionally, for XHTML inputs some characters which turned out to be erroneous in the documents to be converted are corrected:

* U+00A4 CURRENCY SIGN (¤) is replaced with U+00F1 LATIN SMALL LETTER N WITH TILDE (ñ)
* U+0303 COMBINING TILDE (̃) is replaced with U+0342 COMBINING GREEK PERISPOMENI (͂)
* U+02CD MODIFIER LETTER LOW MACRON (ˍ), U+00A6 BROKEN BAR (¦) and U+00BF INVERTED QUESTION MARK (¿) are removed

Finally, all text is normalized to Unicode normal form NFC.

### Hyphenation Heuristic

Many TEI documents in the KOLIMO corpus reproduce hyphenation (word-breaking at the end of lines) that (presumably) was present in the non-digital original. In the vast majority of documents where this is the case, the Unicode character U+00AC NOT SIGN (¬) is used to encode the word-break hyphen; therefore, this character and any white space (including line breaks) following it is removed so that the broken word is merged again with its remainder in the next line. For example,

	<hi rendition="#in">I</hi>n Front des ſchon ſeit Kurfürſt Georg Wil¬<lb/>
	helm von der Familie von Brieſt bewohnten Herren¬<lb/>
	hauſes zu Hohen-Cremmen fiel heller Sonnenſchein<lb/>
	auf die mittagsſtille Dorfſtraße

is converted to the following plain text:

	In Front des schon seit Kurfürst Georg Wilhelm von der Familie von Briest bewohnten Herrenhauses zu Hohen-Cremmen fiel heller Sonnenschein
	auf die mittagsstille Dorfstraße

(The line break where no hyphenation occurs is preserved, the line breaks with broken words disappear.)

There is a comparatively small number of TEI documents in KOLIMO where not this special character, but an ordinary ASCII hyphen (-, U+002D HYPHEN-MINUS) is used. Unfortunately, this character also fulfils some other functions and cannot generally be treated like the above. To determine whether an ASCII hyphen is a word-break hyphen and should be removed, this tool implements the following heuristic, which is only applied when converting TEI and only if the input document does not contain any U+00AC NOT SIGN (¬):

* Hyphens in the middle of lines are never word-break hyphens; a word-break hyphen can only occur immediately before a line break (in the form of a line break character such as U+000A or encoded as `<lb/>`);
* if the first word in the next line starts with a capial letter, the hyphen is preserved (<i lang="de">Cigaretten-Parfüm</i>, <i lang="de">Bibel-Capitel</i>);
* if the first full word in the next line is <i lang="de">und</i> or <i lang="de">oder</i>, the word before the hyphen is assumed to be a truncated prefix and the hyphen is not removed (<i lang="de">Wein- und Spielnacht</i>, <i lang="de">gleich- oder mehrwerthigen</i>);
* all other hyphens at the end of lines are considered word-break hyphens.

Example:

	auf dem Tiſche und den nächſten Stühlen herum-<lb/>
	lagen, bückte ſich nach einem Journal, das ihm<lb/>
	entglitten war, und ſchleppte die papierne Bürde

becomes

	auf dem Tische und den nächsten Stühlen herumlagen, bückte sich nach einem Journal, das ihm
	entglitten war, und schleppte die papierne Bürde

(Again, the line break where no hyphenation occurs is preserved, the line break with a broken word disappears.)

	Im Zimmer machte ſich ſchon das Cigaretten-<lb/>
	Parfüm deutlich riechbar.

becomes

	Im Zimmer machte ſich ſchon das Cigaretten-Parfüm deutlich riechbar.

(The line break after the hyphen is still removed so that there is not whitespace after it.)

	Er hatte die phyſio-<lb/>
	logiſchen Nachwirkungen jener durchgenoſſenen Wein-<lb/>
	und Spielnacht über ſich ergehen laſſen müſſen.

becomes

	Er hatte die physiologischen Nachwirkungen jener durchgenossenen Wein- und Spielnacht über sich ergehen lassen müssen.

(No line break, but whitespace.)

## Known Problems

### Footnotes

Footnote text appears in the output at the position where it is encountered in the input. In most documents in KOLIMO, including all XHTML files, footnote texts are given where (in a printed work) the footnote *mark* would be placed; in some TEI documents, footnotes are placed at the end of the document and referenced from within the text. In the latter case, the footnote text appears at the end of the document in the output, too. In the former case, the footnote text appears in the middle of the output text, which may lead to problems when processing the text. In `human` conversion mode, such footnote texts are surrounded by <span lang="de">`[Fußnote: ...]`</span>, while in `tools` conversion mode no extra text is added (not even spaces).