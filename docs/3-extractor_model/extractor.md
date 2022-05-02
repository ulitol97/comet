---
id: extractor
title: Extractor model
---

# Extractor model

At this point, it's been established how Validators are exclusively in charge of
validating a stream of RDF data according to their configuration. However, the
input data that is fed to the validators must come from somewhere...

Enter the _**extractor model**_.

## Definition

@APP_NAME@ extractors are code blocks capable of generating a stream of RDF
data, which is eventually shoved into a validator. From the validator's
perspective, it does not care which extractor the data comes from or how the
extractor got that data (e.g.: from a file, from an in-memory list, from a Kafka
stream, etc.).

@APP_NAME@ defines the
abstract _[StreamExtractor](https://ulitol97.github.io/comet/scaladoc/org/ragna/comet/stream/extractors/StreamExtractor.html)_
class from which all extractors are derived.

```scala title="Signature of the StreamExtractor class"
 abstract class StreamExtractor[A](
    val format: DataFormat,
    val inference: InferenceEngine = defaultInferenceEngine,
    protected val concurrentItems: Int = defaultConcurrentParsing,
    protected val itemTimeout: Option[FiniteDuration] = defaultIncomingItemsTimeout
  )
  (
    implicit private val toRdfElement: A => RDFElement
  )
```

## Configuration

Regardless of their specific behaviour, all extractors receive some
configuration parameters:

### Mandatory parameters

#### Data format

_[DataFormat](https://ulitol97.github.io/comet/scaladoc/org/ragna/comet/data/DataFormat$.html)_
of the RDF data that will be processed by the extractor.

> The extractor expects all data to share the same format.

### Optional parameters

#### Data inference

- **Purpose**: Inference of the RDF data that will be processed by the
  extractor.
- **Default value**: No inference.

> The extractor expects all data to share the same inference.

#### Concurrent items

- **Purpose**: Define the maximum number of items than the extractor can fetch
  and parse for RDF in parallel.
- **Default value**: `10`

#### Item timeout

- **Purpose**: Optionally, the amount of time that this extractor must wait
  without receiving any item before halting the stream. This is specially useful
  to prevent starvation when the data comes from remote/unreliable sources.
- **Default value**: No timeout.

## Generic type

Whilst creating an extractor, a template type `A` must be defined (or inferred
by the compiler), where `A` is the type of the items that the extractor expects
to receive.

This option is provided for maximum flexibility and customization. For instance,
imagine that you are receiving RDF data from a remote Kafka stream. The incoming
data could arrive in one of two ways:

- Encoded as Strings of RDF data: safe and easy to decode, but limited.
- Encoded as instances of a custom class: may contain further metadata,
  utilities, etc.

In order not to limit the extractors to processing RDF that is already serialized as
Strings, you may specify the following:

1. The generic type `A`.
2. An implicit function `toRdfElement`, telling @APP_NAME@ how to convert the
incoming instances of `A` to @APP_NAME@'s own _[RDFElement](https://ulitol97.github.io/comet/scaladoc/org/ragna/comet/model/rdf/RDFElement.html)_ type.

```scala title="Signature of toRdfElement"
implicit private val toRdfElement: A => RDFElement
```