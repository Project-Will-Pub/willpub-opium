# OPIUM - Operate `pub` interface under machine.

[![Test](https://github.com/Project-Will-Pub/willpub-opium/actions/workflows/test.yml/badge.svg?branch=main)](https://github.com/Project-Will-Pub/willpub-opium/actions/workflows/test.yml)
[![CodeQL](https://github.com/Project-Will-Pub/willpub-opium/actions/workflows/codeql.yml/badge.svg?branch=main)](https://github.com/Project-Will-Pub/willpub-opium/actions/workflows/codeql.yml)
[![Maven Central](https://img.shields.io/maven-central/v/xyz.rk0cc.willpub/opium.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22xyz.rk0cc.willpub%22%20AND%20a:%22opium%22)

This package allows operating `pub` command with Java object only and return console context.

## Perquisites

* Create environment variable `WILLPUB_OPIUM_DART_FLUTTER_EXEC`
  * The first field is Dart executable which is mandatory field
  * The second field is Flutter executable which is optional
  * Example: `WILLPUB_OPIUM_DART_FLUTTER_EXEC=/home/user/flutter/bin/dart;/home/user/flutter/bin/flutter`
  * For Windows user, please uses batch file which ended with `.bat`


## Setup

* Maven 3 or above
* JDK 17 or above

## Install

**Maven**

```xml
<dependency>
  <groupId>xyz.rk0cc.willpub</groupId>
  <artifactId>opium</artifactId>
  <version>1.1.0</version>
</dependency>
```

## License

BSD-3
