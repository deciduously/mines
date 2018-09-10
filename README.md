# Mines!
Minesweeper in [Reagent](https://reagent-project.github.io/).  Currently a tad broken.
### Play
Current version available on [deciduously.com](http://deciduously.com/static/extern/mines/index.html).

To build locally, you need `git`, `java`, and `boot`.  Refer to your OS documentation to obtain a Java installation.  To quickly make `boot` available, this [Makefile](https://gist.github.com/deciduously/3451bfc89414c56ef734ceebeeb7db14) provides a `make deps` command which will retrieve the `boot` shim via `curl`, placing it in `./bin`.  It's very small - `boot` will gather the needed dependencies itself on first run.

Then execute:
```shell
git clone https://github.com/deciduously/mines
cd mines/
boot build # bin/boot build if using the above Makefile
```
Open the resulting `target/index.html` file in your browser and settle in for the night.
### Hack
`boot dev` will run a hot-reloading dev server on `localhost:3000`.

You can use this with EMACS/Cider by invoking `cider-jack-in-clojurescript`, waiting a while, opening the repl buffer, and typing `(def p (future (boot (dev))))`, waiting for it to compile, typing `(start-repl)`, pointing your browser to `localhost:3000`, and finally attaching the browser devtools.

The interactivity is *totally* worth the extra 90 seconds of startup time - what are you closing EMACS for, anyway?
