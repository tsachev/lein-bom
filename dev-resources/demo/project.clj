(defproject demo "0.0.1"
            :plugins [[lein-bom "0.1.0-SNAPSHOT"]]
            :bom {:import [[io.netty/netty-bom "4.0.49.Final"]
                           [com.fasterxml.jackson/jackson-bom "2.8.9"]
                           [org.springframework.boot/spring-boot-dependencies "1.5.4.RELEASE"]]}

            :managed-dependencies [[io.netty/netty-buffer "4.1.13.Final"]]
            :dependencies [[org.clojure/clojure "1.8.0"]
                           [io.netty/netty-codec]
                           [com.fasterxml.jackson.core/jackson-core]])
