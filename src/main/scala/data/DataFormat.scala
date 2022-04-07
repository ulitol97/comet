package org.ragna.comet
package data

/**
 * RDF data formats accepted by the application as input data
 */
enum DataFormat(val name: String) {
  case TURTLE extends DataFormat("Turtle")
  case RDFXML extends DataFormat("RDF/XML")
  case JSONLD extends DataFormat("JSON-LD")
  case NTRIPLES extends DataFormat("N-Triples")
  case RDFJSON extends DataFormat("RDF/JSON")
  case TRIG extends DataFormat("TriG")
}
