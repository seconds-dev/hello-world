(ns hi.system
  (:require [hi.http.core]
            [hi.utils :refer [get-env]]
            [juxt.clip.core :as clip]
            [nrepl.server])
  (:gen-class))

(defn system-config
  []
  (cond->
    {:components {:http-server {:start '(hi.http.core/start-server)
                                :stop '(this)}
                  :portal {:start '(hi.utils.portal/start!)
                           :stop '(hi.utils.portal/stop!)}
                  :repl
                  {:start
                   (let [port (or (some->
                                   (get-env "NREPL_PORT")
                                   Integer/parseInt)
                                  4001)]
                     `(nrepl.server/start-server :port ~port
                                                 :bind "0.0.0.0"))
                   :stop '(nrepl.server/stop-server this)}}}))

(defn -main
  [& _args]
  (clip/start (system-config))
  @(promise))
