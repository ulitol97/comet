---
id: custom_extractors
title: Custom extractors
---

# Custom extractors

@APP_NAME@ defines the
abstract _[StreamExtractor](https://ulitol97.github.io/comet/scaladoc/org/ragna/comet/stream/extractors/StreamExtractor.html)_
class from which all extractors are derived.

## Defining custom extractors

In order to define your custom data extractors to feed validators, you must
override, at least, all non-implemented members of the abstract class:

1. <u>source</u>: String constant identifying this type of stream.
2. <u>inputStream</u>: The stream of RDF data that enters this extractor.

> It is highly encouraged to use the pre-defined extractors as reference for
> defining new custom ones.

### Assertions and constraints

The _StreamExtractor_ class provides a method `checkConfiguration` that is
automatically called on instance creation.

This method tests that the extractor configuration is correct. It may be
overridden or extended with further checks that suit the needs of the new
extractor.
