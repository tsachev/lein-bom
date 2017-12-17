(ns lein-bom.aether
  (:require [cemerick.pomegranate.aether :as aether :refer [maven-central]]
            [clojure.java.io :as io]
            [clojure.string :as str])
  (:import (org.eclipse.aether.artifact ArtifactProperties)
           (org.eclipse.aether.resolution ArtifactDescriptorRequest ArtifactDescriptorResult)
           (org.eclipse.aether RepositorySystem RepositorySystemSession)
           (org.eclipse.aether.graph Dependency Exclusion)
           (org.eclipse.aether DefaultRepositorySystemSession)
           (org.eclipse.aether.artifact Artifact)
           (org.eclipse.aether.util.repository SimpleArtifactDescriptorPolicy)))


(def ^Artifact artifact #'aether/artifact)

(def mirror-selector-fn #'aether/mirror-selector-fn)

(def mirror-selector #'aether/mirror-selector)

(def make-repository #'aether/make-repository)

(def repository-system #'aether/repository-system)

(declare dep-spec*)

(defn exclusion-spec
  "Given an Aether Exclusion, returns a lein-style exclusion vector with the
   :exclusion in its metadata."
  [^Exclusion ex]
  (with-meta (-> ex bean dep-spec*) {:exclusion ex}))

(defn dep-spec*
  "Base function for producing lein-style dependency spec vectors for dependencies
   and exclusions."
  [{:keys [groupId artifactId version classifier extension scope optional exclusions]
    :or   {version    nil
           scope      "compile"
           optional   false
           exclusions nil}}]
  (let [group-artifact (apply symbol (if (= groupId artifactId)
                                       [artifactId]
                                       [groupId artifactId]))]
    (vec (concat [group-artifact]
                 (when version [version])
                 (when (and (seq classifier)
                            (not= "*" classifier))
                   [:classifier classifier])
                 (when (and (seq extension)
                            (not (#{"*" "jar"} extension)))
                   [:extension extension])
                 (when optional [:optional true])
                 (when (and scope
                            (not (str/blank? scope))
                            (not= scope "compile"))
                   [:scope scope])
                 (when (seq exclusions)
                   [:exclusions (vec (map exclusion-spec exclusions))])))))


(defn dep-spec
  "Given an Aether Dependency, returns a lein-style dependency vector with the
   :dependency and its corresponding artifact's :file in its metadata."
  [^Dependency dep]
  (let [artifact (.getArtifact dep)]
    (-> (merge (bean dep) (bean artifact))
        dep-spec*
        (with-meta {:dependency dep :file (.getFile artifact)}))))

(defn artifact-descriptors-repository-session [{:keys [repository-system local-repo offline? transfer-listener mirror-selector] :as args}]
  (let [session (aether/repository-session args)]
    (-> ^DefaultRepositorySystemSession session
        (.setArtifactDescriptorPolicy (new SimpleArtifactDescriptorPolicy false false)))))

(defn read-artifact-descriptors* [& {:keys [repositories coordinates files retrieve local-repo
                                            transfer-listener offline? proxy mirrors repository-session-fn]
                                     :or   {retrieve true}}]
  (let [repositories (or repositories maven-central)
        ^RepositorySystem system (repository-system)
        mirror-selector-fn (memoize (partial mirror-selector-fn mirrors))
        mirror-selector (mirror-selector mirror-selector-fn proxy)
        ^RepositorySystemSession session ((or repository-session-fn
                                              artifact-descriptors-repository-session)
                                           {:repository-system system
                                            :local-repo        local-repo
                                            :offline?          offline?
                                            :transfer-listener transfer-listener
                                            :mirror-selector   mirror-selector})
        deps (->> coordinates
                  (map #(if-let [local-file (get files %)]
                          (-> (artifact %)
                              (.setProperties {ArtifactProperties/LOCAL_PATH (.getPath (io/file local-file))}))
                          (artifact %)))
                  vec)
        repositories (vec (map #(let [repo (make-repository % proxy)]
                                  (-> session
                                      (.getMirrorSelector)
                                      (.getMirror repo)
                                      (or repo)))
                               repositories))
        ]
    (for [dep deps]
      (.readArtifactDescriptor system session (ArtifactDescriptorRequest. dep repositories nil)))))

(defn read-artifact-descriptors [& args]
  (let [{:keys [coordinates]} (apply hash-map args)]
    (->> (apply read-artifact-descriptors* args)
         (map
           (fn [coord result]
             {:pre [coord result]}
             (let [m (when
                       (instance? ArtifactDescriptorResult result)
                       {:file (.. ^ArtifactDescriptorResult result getArtifact getFile)})]
               (with-meta
                 coord
                 (merge {:result result} m))))
           coordinates))))
