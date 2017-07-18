(ns lein-bom.aether-test
  (:require [clojure.test :refer :all]
            [lein-bom.aether :as aether]))

(deftest dep-specs-test
  (testing "scope"
    (is (= (aether/dep-spec* {:groupId "com.example" :artifactId "test"}) ['com.example/test]))))
