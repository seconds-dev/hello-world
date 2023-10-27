(ns hi.http.common
  (:require [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.flash :refer [wrap-flash]]
            [ring.middleware.session :as session]
            [ring.middleware.session.cookie :refer [cookie-store]]))

(def session-cookie-store
  (delay
    (cookie-store)))

(def session-options
  (delay
   {:cookie-attrs {:path "/" :secure true :http-only true}
    :cookie-name "hitthespot-system"
    :store @session-cookie-store}))

(defn wrap-session
  [project]
  (fn [handler]
    (fn [req]
      (let [req' (session/session-request req @session-options)
            session (:session req')
            req' (-> req'
                     (assoc :session (get session project)))
            resp (handler req')]
        (session/session-response
         (if (:session resp)
           (assoc resp :session (assoc session project (:session resp)))
           resp)
         req'
         @session-options)))))

(defn middlewares
  [project]
  [wrap-params
   wrap-keyword-params
   (wrap-session project)
   wrap-flash])
