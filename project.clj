(defproject escriba "0.1.0-SNAPSHOT"

  :description "Managing and organizing the print queue for thermal printers"

  :url "https://github.com/macielti/escriba"

  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url  "https://www.eclipse.org/legal/epl-2.0/"}

  :exclusions [amazonica]

  :dependencies [[org.clojure/clojure "1.12.1"]

                 [net.clojars.macielti/common-clj "43.74.74"]
                 [net.clojars.macielti/postgresql-component "2.2.5"]
                 [net.clojars.macielti/service-component "4.4.2"]
                 [net.clojars.macielti/scheduler-component "0.2.2"]

                 ;pedestal
                 [io.pedestal/pedestal.service "0.7.2"]
                 [io.pedestal/pedestal.jetty "0.7.2"]
                 [io.pedestal/pedestal.error "0.7.2"]

                 [com.taoensso/timbre "6.7.1"]

                 [com.github.clj-easy/graal-build-time "1.0.5"]]

  :profiles {:dev {:plugins        [[lein-shell "0.5.0"]
                                    [com.github.liquidz/antq "RELEASE"]
                                    [com.github.clojure-lsp/lein-clojure-lsp "2.0.9"]]

                   :resource-paths ["resources"]

                   :test-paths     ["test/unit" "test/integration" "test/helpers"]

                   :dependencies   [[net.clojars.macielti/common-test-clj "5.2.4"]
                                    [com.github.igrishaev/pg2-migration "0.1.40"]
                                    [http-kit.fake/http-kit.fake "0.2.2"]
                                    [nubank/matcher-combinators "3.9.1"]
                                    [danlentz/clj-uuid "0.2.0"]
                                    [hashp "0.2.2"]]

                   :injections     [(require 'hashp.core)]

                   :aliases        {"clean-ns"     ["clojure-lsp" "clean-ns" "--dry"] ;; check if namespaces are clean
                                    "format"       ["clojure-lsp" "format" "--dry"] ;; check if namespaces are formatted
                                    "diagnostics"  ["clojure-lsp" "diagnostics"] ;; check if project has any diagnostics (clj-kondo findings)
                                    "lint"         ["do" ["clean-ns"] ["format"] ["diagnostics"]] ;; check all above
                                    "clean-ns-fix" ["clojure-lsp" "clean-ns"] ;; Fix namespaces not clean
                                    "format-fix"   ["clojure-lsp" "format"] ;; Fix namespaces not formatted
                                    "lint-fix"     ["do" ["clean-ns-fix"] ["format-fix"]] ;; Fix both

                                    "native"       ["shell"
                                                    "native-image"
                                                    "--no-fallback"
                                                    "--enable-url-protocols=http,https"
                                                    "-march=compatibility"
                                                    "--report-unsupported-elements-at-runtime"

                                                    "--initialize-at-build-time"

                                                    ; postgresql
                                                    "--initialize-at-build-time=org.slf4j.helpers.NOPLoggerFactory"
                                                    "--initialize-at-build-time=org.pg.enums.TxLevel"
                                                    "--initialize-at-build-time=org.pg.enums.CopyFormat"
                                                    "--initialize-at-build-time=org.pg.enums.TXStatus"

                                                    ;;prometheus
                                                    "--initialize-at-run-time=io.prometheus.client.Striped64"

                                                    "-H:ReflectionConfigurationFiles=reflect-config.json"
                                                    "--features=clj_easy.graal_build_time.InitClojureClasses"
                                                    "-Dio.pedestal.log.defaultMetricsRecorder=nil"
                                                    "-jar" "./target/${:uberjar-name:-${:name}-${:version}-standalone.jar}"
                                                    "-H:+UnlockExperimentalVMOptions"
                                                    "-H:+StaticExecutableWithDynamicLibC"
                                                    "-H:Name=./target/${:name}"]}}}

  :main escriba.components)
