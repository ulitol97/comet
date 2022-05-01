---
id: troubleshooting
title: Troubleshooting
---

# Installation troubleshooting

## Resolver issues

@APP_NAME@ binaries are currently hosted
in [Sonatype](https://search.maven.org/search?q=g:io.github.ulitol97).

If _sbt_ is failing to fetch @APP_INNER_NAME@, add the following to your
configuration in `build.sbt`:

```scala
// Add dependency resolvers for Sonatype
resolvers ++= Seq(
    Opts.resolver.sonatypeSnapshots,
    Opts.resolver.sonatypeReleases
  )
```