// @ts-check
// Note: type annotations allow type checking and IDEs autocompletion

/*
* This file contains most settings related to docusaurus, like:
* - Site building settings
* - Site appearance settings
* - Site publishing settings
* */

const lightCodeTheme = require("prism-react-renderer/themes/github");
const darkCodeTheme = require("prism-react-renderer/themes/dracula");

// Used for publishing on GitHub pages, the rest of settings rely on these
const projectOwner = "ulitol97"
const projectName = "comet"

const deployUrl = `https://${projectOwner}.github.io`
const baseUrl = `/${projectName}/`
// Scaladoc url in the website
const scalaDocUrl = `${deployUrl}${baseUrl}scaladoc/org/ragna/comet`

/** @type {import("@docusaurus/types").Config} */
const config = {
  // Base settings
  title: "Comet",
  tagline: "Validation of RDF data streams",
  organizationName: projectOwner,
  projectName: projectName,

  // Routing and web settings
  url: deployUrl,
  baseUrl: baseUrl,
  favicon: "img/favicon.ico",

  // Site generation settings
  onBrokenLinks: "throw",
  onBrokenMarkdownLinks: "warn",

  // Custom fields
  customFields: {scalaDocUrl},

  // Other settings
  trailingSlash: true, // Prevents errors on GH pages and browsing the scaladoc


  // Appearance and sections
  presets: [
    [
      // Use classic preset
      "classic",
      /** @type {import("@docusaurus/preset-classic").Options} */
      ({
        docs: {
          // Look for docs in the Mdoc generated folder of "docs" project
          path: "../comet-docs/target/mdoc",
          // The sidebar of the docs pages is defined here
          sidebarPath: require.resolve("./sidebars.js"),
          // Please change this to your repo
          editUrl: "https://github.com/ulitol97/comet/tree/main/packages/create-docusaurus/templates/shared/"
        },
        // No blog functionality needed
        blog: false,
        theme: {
          // Place custom CSS here
          customCss: require.resolve("./src/css/custom.css")
        }
      })
    ]
  ],

  themeConfig:
  /** @type {import("@docusaurus/preset-classic").ThemeConfig} */
    ({
      image: "img/preview.png",
      colorMode: {
        defaultMode: "light",
        disableSwitch: false,
        respectPrefersColorScheme: true
      },
      navbar: {
        title: "Comet",
        logo: {
          alt: "Comet Logo",
          src: "img/logo.svg"
        },
        items: [
          // Web docs
          {
            to: "/docs", label: "Web docs", position: "left"
          },
          // Scaladoc
          {
            href: scalaDocUrl, label: "Scaladoc", position: "left"
          },
          {
            href: "https://github.com/ulitol97/comet",
            label: "GitHub",
            position: "right"
          }
        ]
      },
      footer: {
        style: "dark",
        links: [
          {
            title: "Collaborators",
            items: [
              {
                label: "WESO Research Group",
                href: "https://www.weso.es/"
              },
              {
                label: "University of Oviedo",
                href: "https://www.uniovi.es/"
              }
            ]
          },
          {
            title: "Community",
            items: [
              {
                label: "GitHub",
                href: "https://github.com/ulitol97"
              },
              {
                label: "Twitter",
                href: "https://twitter.com/ulitol97"
              }
            ]
          },
          {
            title: "Related work",
            items: [
              {
                label: "RDFShape project",
                href: "https://www.weso.es/rdfshape-api/"
              },
              {
                label: "More software by WESO",
                href: "https://www.weso.es/#software"
              }
            ]
          },
          {
            title: "Special thanks",
            items: [
              {
                label: "SHaclEX",
                href: "https://github.com/weso/shaclex/"
              },
              {
                label: "cats-effect",
                href: "https://typelevel.org/cats-effect/"
              },
              {
                label: "FS2",
                href: "https://fs2.io/"
              },
              {
                label: "FS2 Kafka",
                href: "https://fd4s.github.io/fs2-kafka/"
              }
            ]
          }
        ],
        copyright: `Copyright © ${new Date().getFullYear()} Comet - by ulitol97 💙`
      },
      prism: {
        theme: lightCodeTheme,
        darkTheme: darkCodeTheme
      }
    })
};

module.exports = config;
