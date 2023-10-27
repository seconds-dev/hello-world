(ns user
  (:require [juxt.clip.repl :as repl]
            [hi.utils.portal]
            [portal.api]
            [org.httpkit.server]
            [hi.system :as system]
            [clojure.java.io]
            [watchtower.core :as w]
            [hyperfiddle.rcf]
            [sc.api]
            [malli.provider :as mp]
            [malli.dev :as md]
            [malli.dev.pretty :as mdp]
            [clojure.tools.namespace.repl :refer [set-refresh-dirs]]))

(hyperfiddle.rcf/enable!)
(set-refresh-dirs "src")

(defn reset [] (println "System is restarting") (repl/reset))

(defn vs-portal [] (println "Open portal in vscode") (portal.api/open {:launcher :vs-code}))

(defn reset&vs-portal [] (reset) (vs-portal))

#_:clj-kondo/ignore
(defn watch
  []
  (w/watcher ["src/" "resources/"]
             (w/rate 100)
             (w/file-filter (w/extensions :clj :js))
             (w/on-change (bound-fn [f]
                                    (println "Detected files changed: " (str f))
                                    (reset)))))

(defn dev-config
  []
  {:components {}})

(repl/set-init! (fn []
                  (-> (system/system-config)
                      (update :components into (:components (dev-config))))))

(comment
  (watch)
  ;; start to develop or restart the system if code is modified
  (reset)
  (vs-portal)
  )

(defn v 
  "Try to get the selected value from Portal. 
  a convenient function that can be used in the REPL"
  []
  @(last (portal.api/sessions)))

(comment
  (require '[malli.core :as m])
  (m/function-schemas)
  (md/start! {:report (mdp/reporter)})
  (md/stop!)
  (mp/provide 
    ;; samples
    []))
