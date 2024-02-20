(ns hi.projects.demo.unpoly
  (:require [clojure.core.match :as m]
            [clojure.edn :as edn]
            #_[clojure.string :as string]
            [net.cgrand.enlive-html :as html]
            #_[hickory.core :as hickory]
            [hi.utils :refer [url url-pattern]]
            [ring.util.response :as rr]))

;; --------------- State -----------------
(def projects (atom [{:kind "project" :name "demo1" :price 150
                      :company "demo company 1" :id "1" :note "note1"}
                     {:kind "project" :name "demo2" :price 1800
                      :company "demo company 2" :id "2" :note "note2"}
                     {:kind "project" :name "demo3" :price 10
                      :company "demo company 3" :id "3" :note "note3"}]))

(defn update-project
  [id f]
  (swap! projects
         (fn [projs]
           (map (fn [p]
                  (if (= id (:id p))
                    (f p)
                    p))
                projs))))

(defn delete-project
  [id]
  (swap! projects (fn [prods] (filter #(not= (:id %) id) prods))))

(comment
  (delete-project "1")
  (update-project "1" (fn [p] (assoc p :price (* 1.1 (:price p))))))

(def companies (atom [{:kind "company" :name "demo company 1" :id "1"}
                      {:kind "company" :name "demo company 2" :id "2"}
                      {:kind "company" :name "demo company 3" :id "3"}]))

(html/template "unpoly.html" [])

(html/deftemplate <<main-layout>>
  "unpoly.html"
  [{:keys [req headers main]
    :or {headers []}}]

  [:main] main
  [:head] (fn [head] (assoc head :content (concat (:content head) headers)))

  [:a#project-link] (html/set-attr :href
                                   (url req :demo.unpoly/projects)))

#_:clj-kondo/ignore
(html/defsnippet <project-view-note>
  "project-edit-note.html"
  [:#view-note]
  [{req :req project :project}]
  [:div :div] (html/content (:note project))
  [:div :a] (html/set-attr
             :href (url req :demo.unpoly/projects.note {:id (:id project)})
             :up-target ".project-note"))

(html/defsnippet <project-list>
  "projects-list.html"
  [:#projects-list-main]
  [req]
  [:a#new-project-link] (html/set-attr :href (url req :demo.unpoly/projects :? {:action "create"})
                                       :up-size "large"
                                       :up-layer "new"
                                       :up-accept-location "/demo/unpoly/projects"
                                       :up-on-accepted "up.reload('#project-table', { focus: ':main' })")
  
  [:a#expand-link] (html/set-attr
                    :href (url req :demo.unpoly/projects :? {:action "get-rows"})
                    :up-target "#project-table-body:after"
                    ;; :up-target ":none"
                    #_#_:up-on-rendered "console.log('Updated fragment is', document.querySelector('.target'))"
                    :up-history "false"
                    :up-on-rendered "console.log(result)")

  [:#project-table-body [:tr html/first-of-type]]
  (html/clone-for [proj @projects]
                  [:tr [:td (html/nth-child 1)]] (html/content (:name proj))
                  [:tr [:td (html/nth-child 2)]] (html/content (:company proj))
                  [:tr [:td (html/nth-child 3)]] (html/content (str "$" (:price proj)))
                  [:tr [:td (html/nth-child 4)]] (html/content (<project-view-note> {:req req
                                                                                     :project proj}))))

(html/defsnippet <project-comp>
  "projects-new.html"
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
                       :up-accept-location (url-pattern req :demo.unpoly/company)
                       #_#_:up-on-accepted "console.log(value)"
                       :up-on-accepted "up.validate('form', { params: {'partial':true, 'company-id': value.id }})")
                (not= "create" (-> req :params :action))
                (assoc :action (url req :demo.unpoly/project {:id (:id project)})))]
    (apply html/set-attr (interleave (keys attrs) (vals attrs))))

  [:#name] (html/set-attr :value (:name project ""))
  [:#name-error] (when (-> req :flash :project-name-error)
                   (html/remove-class "hidden"))

  [:#company] (html/set-attr :value (:company project ""))
  [:#price] (html/set-attr :value (:price project "")))

#_:clj-kondo/ignore
(html/defsnippet <project-edit-note>
  "project-edit-note.html"
  [:#edit-note]
  [{req :req project :project}]
  [:form] (html/set-attr :action (url req :demo.unpoly/project {:id (:id project)}
                                      :? {:action "update-note"})
                         :method "post"
                         :up-target ".project-note"
                         :up-history "false"
                         #_#_:up-source (url req :demo.unpoly/projects.note {:id (:id project)}))
  [:#note] (html/set-attr :value (:note project "")))

#_:clj-kondo/ignore
(html/defsnippet <company-comp>
  "company-new.html"
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
  (m/match
   req

    {:request-method :get :params {:action "get-rows"}}
    {#_#_:headers {"X-Up-Target" ".project-table-body"}
     :body (apply str (html/emit*
                       ((html/snippet
                         "hi/projects/demo/projects-list.html"
                         [:#project-table :table]
                         []
                         [:tr [:td (html/nth-child 1)]] (html/content "HI")
                         [:tr [:td (html/nth-child 2)]] (html/content "YO")
                         [:tr [:td (html/nth-child 3)]] (html/content "$$$$$")))))}

    {:request-method :post :params {:action "create"}}
    (let [project (-> req :params)]
      (cond
        ;; callback from created company; (tap> project)
        (edn/read-string (:partial project))
        {:body (apply str (^clojure.lang.Seqable (<<main-layout>> {:req req
                                                                   :main (html/content (<project-comp> {:req req :project {:company "yoyoyo"}}))})))}

        ;; error happened
        (= "666" (-> project :price))
        (let [req (merge req {:flash {:project-name-error "true"}})]
          {:body (apply str (^clojure.lang.Seqable (<<main-layout>> {:req req :main (html/content (<project-comp> {:req req :project project}))})))
           :status 422})

        :else
        ;; should insert project
        (rr/redirect (url req :demo.unpoly/projects))))

    {:request-method :get :params {:action "create"}}
    (let [main-template (^clojure.lang.Seqable (<<main-layout>> {:req req :main (html/content (<project-comp> {:req req}))}))]
      {:body (apply str main-template)})

    {:request-method :get}
    (let [main-template (^clojure.lang.Seqable (<<main-layout>> {:req req :main (html/content (<project-list> req))}))]
      {:body (apply str main-template)})

    :else (rr/not-found "Unsupported.")))

(defn project>> [req]
  (m/match
   req

    {:request-method :post :params {:action "update-note"}}
    (do
      (update-project (-> req :path-params :id)
                      (fn [p] (assoc p :note (-> req :params :note))))
      (rr/redirect (url req :demo.unpoly/projects.note {:id (-> req :path-params :id)}
                        :? {:action "view-note"})))

    {:request-method :get}
    (let [main-template (^clojure.lang.Seqable (<<main-layout>> {:req req :main (html/content (<project-comp> {:req req}))}))]
      {:body (apply str main-template)})

    :else (rr/not-found "Unsupported.")))

(defn projects-note>> [req]
  (m/match
   req
    {:request-method :get :params {:action "view-note"}}
    (let [project (first (filter #(= (:id %) (-> req :path-params :id)) @projects))
          main-template (^clojure.lang.Seqable (<<main-layout>> {:req req
                                                                 :main (html/content (^clojure.lang.Seqable (<project-view-note> {:req req
                                                                                                                                  :project project})))}))]
      {:body (apply str main-template)})

    {:request-method :get}
    (let [project (first (filter #(= (:id %) (-> req :path-params :id)) @projects))
          main-template (^clojure.lang.Seqable (<<main-layout>> {:req req
                                                                 :main (html/content (<project-edit-note> {:req req :project project}))}))]
      {:body (apply str main-template)})

    :else (rr/not-found "Unsupported.")))

(defn companies>> [req]
  (m/match
   req
    {:request-method :post :params {:action "create"}}
    (if (= "error" (-> req :params :name))
      (let [company (merge (-> req :params) {:id 3})
            req (merge req {:flash {:project-name-error "true"}})
            main-template (^clojure.lang.Seqable (<<main-layout>> {:req req :main (html/content (<company-comp> {:req req :company company}))}))]
        {:body (apply str main-template)
         :status 422})
      ;; should insert company and redirect
      (rr/redirect (url req :demo.unpoly/company {:id 3})))
    
    {:request-method :get :params {:action "create"}}
    (let [main-template (^clojure.lang.Seqable (<<main-layout>> {:req req
                                                                 :headers [{:tag :script
                                                                            :attrs {:src "https://unpkg.com/hyperscript.org@0.9.12"}
                                                                            :content []}]
                                                                 :main (html/content (<company-comp> {:req req}))}))]
      {:body (apply str main-template)})

    {:request-method :get}
    (let [main-template (^clojure.lang.Seqable (<<main-layout>> {:req req :main (html/content (<company-comp> {:req req}))}))]
      {:body (apply str main-template)})

    :else (rr/not-found "Unsupported.")))

(defn company>> [req]
  (m/match
   req

    {:request-method :get}
    (let [company (first (filter #(= (:id %) (-> req :path-params :id)) @companies))
          main-template (^clojure.lang.Seqable (<<main-layout>> {:req req :main (html/content (<company-comp> {:req req :company company}))}))]
      {:body (apply str main-template)})

    :else (rr/not-found "Unsupported.")))

(defn make-router
  []
  [""
   ["/projects" {:handler projects>> :name :demo.unpoly/projects}]
   ["/projects/:id" {:handler project>> :name :demo.unpoly/project}]
   ["/projects/:id/note" {:handler projects-note>> :name :demo.unpoly/projects.note}]

   ["/companies" {:handler companies>> :name :demo.unpoly/companies}]
   ["/companies/:id" {:handler company>> :name :demo.unpoly/company}]])
