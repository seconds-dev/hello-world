(ns hi.http.core
  (:require [clojure.string :as string]
            [hi.projects.demo.core :as demo]
            [hi.utils :refer [get-env]]
            [org.httpkit.server :as httpkit]
            [reitit.core :as r]
            [reitit.ring :as ring]))


;; Let reitit expands Var.
;; So we can define the handler with Var instead of function.
;; No need to invoke user/reset after changing the handler. Just eval the new
;; code.
(extend-protocol r/Expand
 clojure.lang.Var
   (expand [this _] {:handler (var-get this)}))

(defn router
  []
  [""
   ["/demo" (demo/make-router)]
   ["/ping" (fn [_] {:status 200, :body "pong"})]])

(defn- wrap-default-content-type
  [handler]
  (fn [req]
    (let [resp (handler req)]
      (if (not (get-in resp [:headers "Content-Type"]))
        (cond (and (string? (:body resp))
                   (string/starts-with? (-> resp
                                            :body
                                            (or ""))
                                        "<"))
              (assoc-in resp
               [:headers "Content-Type"]
               "text/html; charset=utf-8")
              :else resp)
        resp))))

(def debug (atom false))

#_(reset! debug false)

(defn- app-handler
  [req]
  (let [handler (-> (ring/ring-handler (ring/router (router))
                                       (fn not-found [_req]
                                         {:status 404 :body "Page not found"}))
                    wrap-default-content-type)]
    (handler req)))

(defn start-server
  []

  (httpkit/run-server #'app-handler
                      {:port (or (some-> (get-env "PORT")
                                         (Integer/parseInt))
                                 28080)
                       :thread 32
                       :max-body (* 8388608 100)
                       ;; The maximum URL size of Chrome is 2MB
                       :max-line (* 1024 1024 2)
                       :error-logger pr
                       :warn-logger pr}))
