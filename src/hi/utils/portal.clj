(ns hi.utils.portal
  (:require [portal.api :as p]))

(defn start!
  []
  (add-tap #'portal.api/submit)
  (p/start {:port 38080
            :host "0.0.0.0"}))

(defn stop!
  []
  (remove-tap #'portal.api/submit)
  (p/stop))

(defn table
  [data]
  (tap>
    (with-meta
      data
      {:portal.viewer/default :portal.viewer/table})))

(defn p [form]
  `(let [result# ~form] 
     (tap> {:form (with-meta 
                    '~form
                    {:portal.viewer/default :portal.viewer/pr-str})
            :value result#})
     result#))
