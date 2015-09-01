(ns spekl-package-manager.check
 (:require [clojure.java.io :as io]
            [clojure.tools.logging :as log]
            [spekl-package-manager.util :as util]
            [spekl-package-manager.constants :as constants]
            [spekl-package-manager.download :as download]
            [spekl-package-manager.package :as package]
            [clojure.core.reducers :as r]
            [org.satta.glob :as glob]
            [clojure.string :as string]
            [clojure.java.shell :as shell]
            )

  )

(defn get-classpath-sep []
  (if (.equals (util/get-my-platform) "windows")
    ";"
    ":"))

(defn configure-classpath []
  (let [cp (*check-configuration* :classpath)]
    (if (= nil cp)
      "."
      (string/join (get-classpath-sep) (util/expand-glob cp)))))


(defcheck default
  ;; run the check
  (log/info  "Running OpenJML in RAC Mode...")

  ;; see if they want to modify the classpath

  (log/info "SPECS!" *specs*)
  
  (run "java"  "-jar" "${openjml:openjml.jar}" "-classpath" (str "\"" (configure-classpath) "\"")  "-esc" *project-files* ))
