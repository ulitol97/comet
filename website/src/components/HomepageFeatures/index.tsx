import React from "react";
import clsx from "clsx";
import styles from "./styles.module.css";

type FeatureItem = {
  title: string;
  Svg: React.ComponentType<React.ComponentProps<"svg">>;
  description: JSX.Element;
};

const FeatureList: FeatureItem[] = [
  {
    title: "Functional",
    Svg: require("@site/static/img/cats-effect.svg").default,
    description: (
      <>
        Built upon the high-performant <a
        href={"https://typelevel.org/cats-effect/"}>cats-effect</a> framework,
        comet gets the best of the Typelevel ecosystem
      </>
    ),
  },
  {
    title: "Parallel",
    Svg: require("@site/static/img/fs2.svg").default,
    description: (
      <>
        Data streams are defined and composed in terms of <a
        href={"https://fs2.io/"}>FS2</a>'s
        streaming API,
        allowing for customizable levels of parallelism
        in item processing and validation
      </>
    ),
  },
  {
    title: "Powered by Scala",
    Svg: require("@site/static/img/scala-icon.svg").default,
    description: (
      <>
        Comet was designed from the ground up to be <a
        href={"https://www.scala-sbt.org/1.x/docs/Cross-Build.html"}>cross-compiled</a> and
        available for
        <br/>
        Scala 2.13.x and Scala 3.1.x
      </>
    ),
  },
];

function Feature({title, Svg, description}: FeatureItem) {
  return (
    <div className={clsx("col col--4")}>
      <div className="text--center">
        <Svg className={styles.featureSvg} role="img"/>
      </div>
      <div className="text--center padding-horiz--md">
        <h3>{title}</h3>
        <p>{description}</p>
      </div>
    </div>
  );
}

export default function HomepageFeatures(): JSX.Element {
  return (
    <section className={styles.features}>
      <div className="container">
        <div className="row">
          {FeatureList.map((props, idx) => (
            <Feature key={idx} {...props} />
          ))}
        </div>
      </div>
    </section>
  );
}
