# cljdebbugs

A Clojure client library for the [Debbugs](https://debbugs.gnu.org/) bug tracking system.

Debbugs is the bug tracking system used by GNU projects, including GNU Emacs. This library provides a clean interface to query bugs and retrieve their status via the SOAP API.

## Features

- Query bugs by package, severity, tags, and other criteria
- Retrieve detailed bug status information
- Clean Clojure-native API with data-driven design
- XML/SOAP handling abstracted away

## Requirements

- Clojure 1.11+
- Java 11+

## Installation

Add to your `deps.edn`:

```clojure
{:deps
 {io.github.conao3/cljdebbugs {:git/tag "v0.1.0" :git/sha "..."}}}
```

## Usage

### Get Bugs

Query bugs by package name:

```clojure
(require '[cljdebbugs.request :as debbugs])

(def gnu-url "https://debbugs.gnu.org/cgi/soap.cgi?WSDL")

;; Get all bugs for a package
(debbugs/get-bugs gnu-url {:package "emacs"})
;; => ("16469" "71284" "57246" ...)
```

### Get Bug Status

Retrieve detailed status for specific bug IDs:

```clojure
(debbugs/get-status gnu-url ["16469" "71284" "57246"])
;; => ({:bug_num ("16469")
;;      :package ("emacs")
;;      :severity ("normal")
;;      :subject ("...")
;;      ...}
;;     ...)
```

## API Reference

### `cljdebbugs.request`

#### `get-bugs [url query]`
Query bugs from a Debbugs instance. Returns a sequence of bug IDs.

- `url` - The SOAP endpoint URL
- `query` - A map of query parameters (e.g., `{:package "emacs"}`)

#### `get-status [url ids]`
Get detailed status for a list of bug IDs. Returns a sequence of status maps.

- `url` - The SOAP endpoint URL
- `ids` - A sequence of bug ID strings

## Development

Run tests:

```bash
clojure -X:test
```

Build:

```bash
clojure -T:build uber
```

## License

This project is available under an open source license.

## Related Projects

- [debbugs.el](https://elpa.gnu.org/packages/debbugs.html) - Emacs Lisp interface to Debbugs
- [Debbugs Documentation](https://debbugs.gnu.org/Developer.html) - Official Debbugs developer documentation
