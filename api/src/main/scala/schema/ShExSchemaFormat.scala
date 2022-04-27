package org.ragna.comet
package schema

/**
 * Schema formats accepted by the application for ShEx schemas
 */
enum ShExSchemaFormat(val name: String) {
  case SHEXC extends ShExSchemaFormat("ShExC")
  case SHEXJ extends ShExSchemaFormat("ShExJ")
}
