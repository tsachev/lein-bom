(ns lein-bom.plugin
  (:require [leiningen.bom :as bom]
            [leiningen.core.project :as project]))

(def meta-merge #'project/meta-merge)

(defn middleware [project]
  (if-let [managed (bom/manage-dependencies project)]
    (meta-merge project managed)
    project))
