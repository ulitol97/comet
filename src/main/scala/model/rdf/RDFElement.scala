package org.ragna.comet
package model.rdf

/**
 * Simple model class representing an RDF element at its simplest
 *
 * @param rdf String of RDF data
 */
sealed class RDFElement(rdf: String) {
  /**
   * String content of the RDF element, stripped of whitespaces
   */
  val content: String = rdf.strip()
}

object RDFElement {
  /**
   * Instantiate an [[RDFElement]] from a String of data
   * @param inputRdf Input RDF String
   * @return [[RDFElement]] with [[inputRdf]] as its content
   */
  def apply(inputRdf: String): RDFElement = new RDFElement(inputRdf)
}
