# lein-bom

[![Build Status](https://travis-ci.org/tsachev/lein-bom.svg?branch=master)](https://travis-ci.org/tsachev/lein-bom)
[![codecov.io](https://codecov.io/github/tsachev/lein-bom/coverage.svg?branch=master)](https://codecov.io/github/tsachev/lein-bom?branch=master)

[![Clojars Project](https://clojars.org/lein-bom/latest-version.svg)](https://clojars.org/lein-bom)

A Leiningen plugin that provides support for importing Maven "Bill Of Materials" (BOM) dependencies.

## Install

Put `[lein-bom "0.1.0-SNAPSHOT"]` into the `:plugins` vector of your `project.clj`.

## Usage

Specify bom dependencies using `:import` vector in `:bom` map of your `project.clj`.

```clojure
:bom {:import [[io.netty/netty-bom "2.8.9"]]}
```

To see actual managed dependencies, run:

```
$ lein bom
```

## License

Copyright © 2017

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
