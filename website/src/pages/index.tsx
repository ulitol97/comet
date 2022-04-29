import React from "react";
import clsx from "clsx";
import Layout from "@theme/Layout";
import Link from "@docusaurus/Link";
import useDocusaurusContext from "@docusaurus/useDocusaurusContext";
import styles from "./index.module.css";
import HomepageFeatures from "@site/src/components/HomepageFeatures";

function HomepageHeader({buttons}) {
  const {siteConfig} = useDocusaurusContext();
  return (
    <header className={clsx("hero hero--primary", styles.heroBanner)}>
      <div className="container">
        <h1 className="hero__title">{siteConfig.title}</h1>
        <p className="hero__subtitle">{siteConfig.tagline}</p>

        <div className={`${styles.buttons} homepage-buttons`}>
          {/* Print all buttons */}
          {buttons.map((btn, idx) => (
            <Link key={idx}
                  className="button button--secondary button--lg side-margin"
                  to={btn.url}>
              {btn.title}
            </Link>
          ))}
        </div>

      </div>
    </header>
  );
}

export default function Home(): JSX.Element {
  const {siteConfig} = useDocusaurusContext();
  return (
    <Layout
      title="Home" // {siteConfig.title}
      description={`Homepage of Comet Validation API. ${siteConfig.tagline}`}>
      {/* Header includes several buttons/links */}
      <HomepageHeader buttons={[
        {
          title: "Get started",
          url: "/docs"
        },
        {
          title: "RDFShape DEMO",
          url: "https://rdfshape.weso.es/"
        }
      ]}/>
      <main>
        <HomepageFeatures/>
      </main>
    </Layout>
  );
}
