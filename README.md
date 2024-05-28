# Codo Solid Squint
Hi this is a repo containing the Codo frontend App (and some tools to set up a local dev env).

This version is based on SolidJs for rendering, Squint-cljs as a lightweight ClojureScript alternative, WindiCSS for css and Vite for packaging.

We use SolidJs Memoization to pull in normalized data for components and build a very basic frontend database using some custom normalization functions.

The "backend" is based on ComposeDB.

## Getting started
First install pnpm:

```bash
curl -fsSL https://get.pnpm.io/install.sh | sh -
```

select the latest version of node and install ceramic and composedb clis globally:
```bash
pnpm env use --global latest
pnpm install -g @ceramicnetwork/cli
pnpm install -g @composedb/now
```

cli install project local deps and start the dev env:
```bash
pnpm i
pnpm dev
```

now start editing files to see squint compiling things and vite hot reloading the new files under "./dist".

Also browse to localhost:3000/#/tb or /#/profile to actually see something...
