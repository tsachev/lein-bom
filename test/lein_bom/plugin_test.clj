(ns lein-bom.plugin-test
  (:require [leiningen.core.project :as project]
            [lein-bom.plugin :as plugin]
            [clojure.test :refer :all]))

(deftest middleware-test
  (testing "managed dependencies added"
    (let [original (project/read "dev-resources/demo/project.clj")
          actual (plugin/middleware original)]
      (is (> (count (:managed-dependencies actual)) (count (:managed-dependencies original)))))))
