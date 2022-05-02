/**
 * Creating a sidebar enables you to:
 - create an ordered group of docs
 - render a sidebar for each doc of that group
 - provide next/previous navigation

 The sidebars can be generated from the filesystem, or explicitly defined here.

 Create as many sidebars as you want.
 */

// @ts-check

// Cannot be imported outside of module
const scalaDocUrl = "https://ulitol97.github.io/comet/scaladoc/org/ragna/comet";

/** @type {import("@docusaurus/plugin-content-docs").SidebarsConfig} */
const sidebars = {
  // Generate a sidebar from the docs' folder structure
  // sidebar: [{type: "autogenerated", dirName: "."}],

  // Create a sidebar manually
  docsSidebar: [

    /* Category: Installation */
    {
      type: "category",
      label: "Installation",
      items: ["installation/instructions", "installation/troubleshooting"],
      collapsed: false
    },
    
    /* Getting started */
    {
      type: "doc", id: "getting_started/example", label: "Getting started"
    },

    /* Category: Validators */
    {
      type: "category",
      label: "Validators",
      items: ["validators/validator", "validators/validator_config", "validators/validator_results"],
      collapsed: false
    },

    /* Category: Extractor model */
    {
      type: "category",
      label: "Extractor model",
      items: ["extractor_model/predefined_extractors", "extractor_model/custom_extractors"],
      collapsed: false
    },

    /* Category: Error handling */
    {
      type: "category",
      label: "Error handling",
      items: ["error_handling/predefined_errors", "error_handling/other_errors"],
      collapsed: true
    },

    /* Category: Testing */
    {
      type: "category",
      label: "Testing and auditing",
      items: ["testing_auditing/unit-tests", "testing_auditing/logging"],
      collapsed: true
    },

    /* Category: documentation (just a link to Scaladoc) */
    {
      type: "category",
      label: "Additional documentation",
      items: [{
        type: "link", label: "Scaladoc", href: scalaDocUrl
      }],
      collapsed: false
    },

    /* Webpage information */
    {
      type: "doc", id: "webpage_info/info", label: "About this webpage"
    }]
};

module.exports = sidebars;
