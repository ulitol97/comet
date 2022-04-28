import React from "react"
import {Redirect} from "@docusaurus/router"
import useBaseUrl from "@docusaurus/useBaseUrl"

// Redirect to docs welcome page when /docs is requested
const CometDocs = () => {
  const redirectDocsIntro = useBaseUrl("/docs/home")
  return <Redirect to={redirectDocsIntro}/>
}

export default CometDocs