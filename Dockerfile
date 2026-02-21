FROM container-registry.oracle.com/graalvm/native-image:23 as build-health-cli

RUN microdnf install -y git

RUN curl -O https://raw.githubusercontent.com/technomancy/leiningen/stable/bin/lein && \
    chmod +x lein && \
    mv lein /usr/bin/lein && \
    lein upgrade

RUN git clone https://github.com/macielti/health-cli

WORKDIR ./health-cli

RUN lein do clean, uberjar, native

FROM container-registry.oracle.com/graalvm/native-image:23 as build

RUN curl -O https://raw.githubusercontent.com/technomancy/leiningen/stable/bin/lein && \
    chmod +x lein && \
    mv lein /usr/bin/lein && \
    lein upgrade

COPY . /usr/src/app

WORKDIR /usr/src/app

RUN lein do clean, uberjar, native

FROM gcr.io/distroless/base:latest

WORKDIR /app

COPY --from=build-health-cli /app/health-cli/target/health-cli  /app/health-cli

COPY --from=build /usr/src/app/target/escriba  /app/escriba

CMD ["./escriba"]
