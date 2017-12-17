(defproject demo "0.0.1"
            :bom {:import [[io.netty/netty-bom "4.0.54.Final"]
                           [com.fasterxml.jackson/jackson-bom "2.9.3"]
                           [org.springframework.boot/spring-boot-dependencies "1.5.9.RELEASE"]]}

            :managed-dependencies [[io.netty/netty-buffer "4.1.19.Final"]]
            :dependencies [[org.clojure/clojure "1.9.0"]
                           [io.netty/netty-codec]
                           [com.fasterxml.jackson.core/jackson-core]])
