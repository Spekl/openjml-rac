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



(defn configure-classpath [extra]
  (let [cp (*check-configuration* :classpath)]
    (if (= nil cp)
      (str  "."  (get-classpath-sep) extra)
      (string/join (get-classpath-sep) (cons extra (util/expand-glob cp))))))

(defn configure-specspath []
  (string/join (get-classpath-sep) (doall (map (fn [x] (.getPath (x :dir))) *specs*))))

(defn get-main []
  (let [m (*check-configuration* :main)]
    (if (= nil m)
      (do (log/error "Please specify the configuration element 'main' which is a class containing a entrypoint for your application.") (System/exit 1))
      m)))

(defn has-specs []
  (> (count *specs*) 0))


(defn get-out-dir []
  (if (not (= nil (*check-configuration* :out)))
    (list "-d" (*check-configuration* :out))))

(defn get-classpath [extra]
  (if (= nil (*check-configuration* :out))
    (list  "-classpath" (str "\"" (configure-classpath extra)  "\""))
    (list  "-classpath" (str "\"" (configure-classpath extra) (get-classpath-sep) (*check-configuration* :out) "\""))))

(defn get-specspath []
  (if (has-specs)
    (list  "-specspath" (str "\"" (configure-specspath) "\""))))

;; figure out the outdir, classpath, specspath 
(defn get-extra-args []
  (filter (fn [x] (not (= nil x))) (flatten (list (get-out-dir) (get-classpath ".") (get-specspath)))))

(defcheck rac-check
  (log/info  "Running OpenJML RAC Program...")
  (run "java" (get-classpath (resolve-path "openjml" "jmlruntime.jar")) (get-main) ))

(defcheck rac-compile
  (log/info  "Running OpenJML RAC Compile...")
  (run "java"  "-jar" "${openjml:openjml.jar}" (get-extra-args)  "-rac" *project-files* ))

(defcheck default (rac-compile))

