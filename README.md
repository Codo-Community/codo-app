# Codo Solid Squint
Hi this is a repo containing the Codo frontend App (and some tools to set up a local dev env).

This version is based on SolidJs for rendering, Squint-cljs as a lightweight ClojureScript alternative, WindiCSS for css and Vite for packaging.

We use Memoization and build a very basic frontend database using some custom normalization functions.

The "backend" is based on ComposeDB.

WIP: setting up composedb dev env script

## Getting started

```bash
pnpm i
pnpm run dev
```

now start editing files to see squint compiling things and vite hot reloading the new files under "./dist".

Also browse to localhost:3000/#/tb or /#/counter to actually see something...
