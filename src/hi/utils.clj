(ns hi.utils
  (:require [reitit.core :as r]
            [clojure.core.match :refer [match]]))

(defn get-env [k] (System/getenv k))

(defn url 
  "Usage example:
  (url req :seconds/styles :? {:shop \"shop1\"})
  (url req :seconds/styles {:id 1} :? {:shop \"shop1\"}) => 
  "
  [req rname & args]
  (let [router (:reitit.core/router req)]
    (match (into [] args)
           ;;
           [path-params :? query-params]
           (some-> router
                   (r/match-by-name rname path-params)
                   (r/match->path query-params))

           ;;
           [path-params]
           (some-> router
                   (r/match-by-name rname path-params)
                   r/match->path)

           ;;
           [:? query-params]
           (some-> router
                   (r/match-by-name rname)
                   (r/match->path query-params))

           ;;
           :else
           (some-> router
                   (r/match-by-name rname)
                   r/match->path))))
