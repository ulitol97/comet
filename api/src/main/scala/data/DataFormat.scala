package org.ragna.comet
package data

/**
 * Abstract class representing the available formats for RDF data
 *
 * @note Used as an enum-replacement for Scala 2 syntax
 * @see [[https://stackoverflow.com/a/71206847/9744696]]
 */
sealed abstract class DataFormat(val name: String)

/**
 * Enumeration of the RDF data formats accepted by the application as input data
 */
object DataFormat {
  case object TURTLE extends DataFormat("Turtle")

  case object RDFXML extends DataFormat("RDF/XML")

  case object JSONLD extends DataFormat("JSON-LD")

  case object NTRIPLES extends DataFormat("N-Triples")

  case object RDFJSON extends DataFormat("RDF/JSON")

  case object TRIG extends DataFormat("TriG")
}
