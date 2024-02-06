(ns hi.projects.demo.unpoly
  (:require [clojure.core.match :as m]
            [clojure.edn :as edn]
            #_[clojure.string :as string]
            [net.cgrand.enlive-html :as html]
            #_[hickory.core :as hickory]
            [hi.utils :refer [url]]
            [ring.util.response :as rr]))

;; --------------- State -----------------
(def projects (atom [{:kind "project" :name "demo1" :price 150 :company "demo company 1" :id "1"}
                     {:kind "project" :name "demo2" :price 1800 :company "demo company 2" :id "2"}
                     {:kind "project" :name "demo3" :price 10 :company "demo company 3" :id "3"}]))

(def companies (atom [{:kind "company" :name "demo company 1" :id "1"}
                      {:kind "company" :name "demo company 2" :id "2"}
                      {:kind "company" :name "demo company 3" :id "3"}]))

(html/template "hi/projects/demo/unpoly.html" [])

#_:clj-kondo/ignore
(html/deftemplate <<main-layout>>
  "hi/projects/demo/unpoly.html"
  [req main]

  [:main] main
  [:a#project-link] (html/set-attr :href
                                   (url req :demo.unpoly/projects)))

#_:clj-kondo/ignore
(html/defsnippet <project-list>
  "hi/projects/demo/projects-list.html"
  [:#projects-list-main]
  [req]
  [:a#new-project-link] (html/set-attr :href (url req :demo.unpoly/projects :? {:action "create"})
                                       :up-size "large"
                                       :up-layer "new"
                                       :up-accept-location "/demo/unpoly/projects"
                                       :up-on-accepted "up.reload('#project-table', { focus: ':main' })")

  [:#project-table-body [:tr html/first-of-type]]
  (html/clone-for [proj @projects]
                  [:tr [:td (html/nth-child 1)]] (html/content (:name proj))
                  [:tr [:td (html/nth-child 2)]] (html/content (:company proj))
                  [:tr [:td (html/nth-child 3)]] (html/content (str "$" (:price proj)))))

#_:clj-kondo/ignore
(html/defsnippet <project-comp>
  "hi/projects/demo/projects-new.html"
  [:#projects-comp-main]
  [{req :req project :project}]
  [:form] (html/set-attr :action (if (or (= "new" (-> req :params :action))
                                         (nil? (-> req :path-params :id)))
                                   (url req :demo.unpoly/projects :? {:action "create"})
                                   (url req :demo.unpoly/project {:id (:id project)}))
                         :method "post"
                         :up-submit "")

  [:#new-company-link]
  (let [attrs (cond-> {:href (url req :demo.unpoly/companies :? {:action "create"})
                       :up-size "large"
                       :up-layer "new"}
                (or (= "create" (-> req :params :action))
                    (nil? (-> req :path-params :id)))
                (assoc :action (url req :demo.unpoly/products :? {:action "create"})
                       :up-accept-location "/demo/unpoly/companies/$id"
                       :up-on-accepted "up.validate('form', { params: {'partial':true, 'company-id': value.id } })")
                (not= "create" (-> req :params :action))
                (assoc :action (url req :demo.unpoly/project {:id (:id project)})))]
    (apply html/set-attr (interleave (keys attrs) (vals attrs))))

  [:#name] (html/set-attr :value (:name project ""))
  [:#name-error] (when (-> req :flash :project-name-error)
                   (html/remove-class "hidden"))

  [:#company] (html/set-attr :value (:company project ""))
  [:#price] (html/set-attr :value (:price project "")))

#_:clj-kondo/ignore
(html/defsnippet <company-comp>
  "hi/projects/demo/company-new.html"
  [:#company-comp-main]
  [{req :req company :company}]
  [:form] (html/set-attr :action (if (= "create" (-> req :params :action))
                                   (url req :demo.unpoly/companies :? {:action "create"})
                                   (url req :demo.unpoly/company {:id (:id company)}))
                         :method "post"
                         :up-submit "")

  [:#name] (html/set-attr :value (:name company ""))
  [:#name-error] (when (-> req :flash :project-name-error)
                   (html/remove-class "hidden")))

(defn projects>> [req]
  (def req req)
  (m/match
   req

    {:request-method :post :params {:action "create"}}
    (let [project (-> req :params)]
      (cond
        (edn/read-string (:partial project))
        {:body (apply str (<<main-layout>> req (html/content (<project-comp> {:req req :project {:company "yoyoyo"}}))))}

        (= "666" (-> project :price))
        (let [req (merge req {:flash {:project-name-error "true"}})]
          {:body (apply str (<<main-layout>> req (html/content (<project-comp> {:req req :project project}))))
           :status 422})

        :else
        (rr/redirect (url req :demo.unpoly/projects))))

    {:request-method :get :params {:action "create"}}
    (let [main-template (<<main-layout>> req (html/content (<project-comp> {:req req})))]
      {:body (apply str main-template)})

    {:request-method :get}
    (let [main-template (<<main-layout>> req (html/content (<project-list> req)))]
      {:body (apply str main-template)})

    :else (rr/not-found "Unsupported.")))

(defn project>> [req]
  (let [main-template (<<main-layout>> req (html/content (<project-comp> {:req req})))]
    {:body (apply str main-template)}))

#_(defn projects-edit>> [req]
    (m/match
     req

      {:request-method :post :params {:action "update"}}
      (let [project (first (filter #(= (:id %) (-> req :path-params :id)) @projects))
            req (merge req {:flash {:project-name-error "true"}})
            main-template (html/template "hi/projects/demo/unpoly.html" []
                                         [:main] (html/content (<project-comp> {:req req :project project})))]
        {:body (apply str (main-template))
         :status 422})

      {:request-method :get}
      (let [project (first (filter #(= (:id %) (-> req :path-params :id)) @projects))
            main-template (html/template "hi/projects/demo/unpoly.html" []
                                         [:main] (html/content (<project-comp> {:req req :project project})))]
        {:body (apply str (main-template))})

      :else (rr/not-found "Unsupported.")))

(defn companies>> [req]
  (m/match
   req
    {:request-method :post :params {:action "create"}}
    (if (= "error" (-> req :params :name))
      (let [company (merge (-> req :params) {:id 3})
            req (merge req {:flash {:project-name-error "true"}})
            main-template (<<main-layout>> req (html/content (<company-comp> {:req req :company company})))]
        {:body (apply str main-template)
         :status 422})
      (rr/redirect (url req :demo.unpoly/company {:id 3})))
    
    {:request-method :get :params {:action "create"}}
    (let [main-template (<<main-layout>> req (html/content (<company-comp> {:req req})))]
      {:body (apply str main-template)})

    {:request-method :get}
    (let [main-template (<<main-layout>> req (html/content (<company-comp> {:req req})))]
      {:body (apply str main-template)})

    :else (rr/not-found "Unsupported.")))

(defn company>> [req]
  (m/match
   req

    {:request-method :get}
    (let [company (first (filter #(= (:id %) (-> req :path-params :id)) @companies))
          main-template (<<main-layout>> req (html/content (<company-comp> {:req req :company company})))]
      {:body (apply str main-template)})

    :else (rr/not-found "Unsupported.")))

#_(defn companies-edit>> [req]
    (m/match
     req

      {:request-method :post :params {:action "update"}}
      (let [project (first (filter #(= (:id %) (-> req :path-params :id)) @companies))
            req (merge req {:flash {:project-name-error "true"}})
            main-template (html/template "hi/projects/demo/unpoly.html" []
                                         [:main] (html/content (<company-comp> {:req req :project project})))]
        {:body (apply str (main-template))
         :status 422})

      {:request-method :get}
      (let [company (first (filter #(= (:id %) (-> req :path-params :id)) @companies))
            main-template (html/template "hi/projects/demo/unpoly.html" []
                                         [:main] (html/content (<company-comp> {:req req :company company})))]
        {:body (apply str (main-template))})

      :else (rr/not-found "Unsupported.")))

(defn make-router
  []
  [""
   ["/projects" {:handler projects>> :name :demo.unpoly/projects}]
   ["/projects/:id" {:handler project>> :name :demo.unpoly/project}]
   #_["/projects/:id/edit" {:handler projects-edit>> :name :demo.unpoly/projects.edit}]

   ["/companies" {:handler companies>> :name :demo.unpoly/companies}]
   ["/companies/:id" {:handler company>> :name :demo.unpoly/company}]
   #_["/companies/:id/edit" {:handler companies-edit>> :name :demo.unpoly/companies.edit}]])
