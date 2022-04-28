<!--suppress HtmlDeprecatedAttribute -->
<div align="center">
  <img alt="comet logo" src="https://user-images.githubusercontent.com/35763574/162334885-228fc9fc-8e28-4b80-83e7-ace6c8fb1190.png"/>
</div>

<br/>

Comet is a [Scala](https://scala-lang.org/) application for
validating [RDF data](https://www.w3.org/RDF/)
streams. Comet uses:

- [Cats Effect](https://github.com/typelevel/cats-effect): For composing the app
  in a functional style and within the Typelevel ecosystem
- [FS2](https://github.com/typelevel/fs2): For reliably processing all sorts of
  Streams in parallel
- [SHaclEX](https://github.com/weso/shaclex): For RDF processing and validation
  against both [ShEx](https://shex.io/)
  and [SHACL](https://www.w3.org/TR/shacl/)

## Downloading and installing

- Todo.
    - Explain how to get the library once uploaded.
    - Cross compiled versions.
      - Scala 2.13 and Scala 3.1
      - Some Scala 3 syntax is not used to provide this cross-compatibility
    - Example build.sbt adding the dependency.

## Getting started

- Todo.
    - Introduce a little example in a commented main method.

## Developer info

For those who are willing to build, modify or contribute, the project uses _sbt_
, as well as:

- Java 17 (LTS)
- [Scala 3](https://docs.scala-lang.org/scala3/new-in-scala3.html)
- Cats Effect 3

## Pending explanations

- Todo. Refer to wiki/microsite for:
    - Usage examples
    - Validator model.
    - Extractor model (extensible!).
    - etc.
