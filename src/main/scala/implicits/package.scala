package org.ragna.comet

import model.rdf.RDFElement

import scala.language.implicitConversions

/**
 * Application-wide implicits, contained in a unique central object
 */
package object implicits {
  /**
   * Implicit conversions for mapping items to [[RDFElement]]
   */
  object RDFElementImplicits {
    /**
     * Instantiate an [[RDFElement]] from a String of data
     *
     * @param input Input string presumably containing RDF
     * @return [[RDFElement]] with [[input]] as its content
     */
    implicit def rdfFromString(input: String): RDFElement = RDFElement(input)
  }
}
