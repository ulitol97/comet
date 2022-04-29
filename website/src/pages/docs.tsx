import React from "react"
import {Redirect} from "@docusaurus/router"
import useBaseUrl from "@docusaurus/useBaseUrl"

// Redirect to a docs startup page when /docs is requested
// Use "/docs/{ID_OF_TARGET_DOC}" as the redirection URL
const CometDocs = () => {
  const redirectDocsIntro = useBaseUrl("/docs/installation/instructions")
  return <Redirect to={redirectDocsIntro}/>
}

export default CometDocs