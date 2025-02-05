FROM fedora:latest as base
RUN curl -fsSL https://get.pnpm.io/install.sh | bash -
ENV PNPM_HOME="/pnpm"
ENV PATH="$PNPM_HOME:/root/.local/share/pnpm:${PATH}"
RUN . /root/.bashrc && pnpm env use --global lts
RUN dnf install -y patch
COPY . /app
WORKDIR /app

FROM base AS dev-deps
RUN --mount=type=cache,id=pnpm,target=/pnpm/store pnpm install --frozen-lockfile

FROM dev-deps as build
RUN --mount=type=cache,id=pnpm,target=/pnpm/store pnpm build

#FROM base AS prod-deps
#RUN --mount=type=cache,id=pnpm,target=/pnpm/store pnpm install --prod --frozen-lockfile
