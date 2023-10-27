(ns hi.projects.demo.core
  (:require
   [hiccup2.core :as h]
   [clojure.core.match :as m]
   [ring.util.response :as rr]
   [hi.http.common :as common]
   [hi.utils :refer [url]]))

(defn- skeleton
  [_req body]
  [:body#body-pd {:class "bg-light"}
   [:div {:class "page"}
    [:div {:class "page-wrapper"}
     [:div {:class "page-body"} [:div {:class "container-xl"} body]]]]])

(defn layout
  [{req :req body :body head :head}]
  [:html
   [:head [:title "歡迎光臨"]
    [:meta {:charset "utf-8"}]
    [:meta {:name "viewport" :content "width=device-width, initial-scale=1.0"}]
    [:meta {:name "robots" :content "noindex,nofollow"}]
    [:link {:rel "stylesheet" :href "https://cdn.jsdelivr.net/npm/@unocss/reset/tailwind.min.css"}]
    [:script {:src "https://cdn.jsdelivr.net/npm/@unocss/runtime/attributify.global.js"}]
    [:script {:src "https://cdn.jsdelivr.net/npm/hyperscript.org@0.9.11/dist/_hyperscript.min.js"}]
    [:script {:src "https://cdn.jsdelivr.net/npm/htmx.org@1.9.5/dist/htmx.min.js"}]
    (for [tag head] tag)]
   (skeleton req body)])

(defn render-view
  [template]
  {:status 200 :body (str (h/raw "<!DOCTYPE html>") (h/html template))})

(defn page1-view
  [req]
  (layout
   {:req req
    :head []
    :body [:section.bg-white.dark:bg-gray-900
           [:div.max-w-2xl.px-4.py-8.mx-auto.lg:py-16
            [:h2.mb-4.text-xl.font-bold.text-gray-900.dark:text-white "Update product"]
            [:form {:action "#"}
             [:div.grid.gap-4.mb-4.sm:grid-cols-2.sm:gap-6.sm:mb-5
              [:div.sm:col-span-2
               [:label.block.mb-2.text-sm.font-medium.text-gray-900.dark:text-white
                {:for "name"}
                "Product Name"]
               [:input#name.bg-gray-50.border.border-gray-300.text-gray-900.text-sm.rounded-lg.focus:ring-primary-600.focus:border-primary-600.block.w-full.p-2.5.dark:bg-gray-700.dark:border-gray-600.dark:placeholder-gray-400.dark:text-white.dark:focus:ring-primary-500.dark:focus:border-primary-500
                {:type "text"
                 :name "name"
                 :value "Apple iMac 27&ldquo;"
                 :placeholder "Type product name"
                 :required ""}]]
              [:div.w-full
               [:label.block.mb-2.text-sm.font-medium.text-gray-900.dark:text-white
                {:for "brand"}
                "Brand"]
               [:input#brand.bg-gray-50.border.border-gray-300.text-gray-900.text-sm.rounded-lg.focus:ring-primary-600.focus:border-primary-600.block.w-full.p-2.5.dark:bg-gray-700.dark:border-gray-600.dark:placeholder-gray-400.dark:text-white.dark:focus:ring-primary-500.dark:focus:border-primary-500
                {:type "text"
                 :name "brand"
                 :value "Apple"
                 :placeholder "Product brand"
                 :required ""}]]
              [:div.w-full
               [:label.block.mb-2.text-sm.font-medium.text-gray-900.dark:text-white
                {:for "price"}
                "Price"]
               [:input#price.bg-gray-50.border.border-gray-300.text-gray-900.text-sm.rounded-lg.focus:ring-primary-600.focus:border-primary-600.block.w-full.p-2.5.dark:bg-gray-700.dark:border-gray-600.dark:placeholder-gray-400.dark:text-white.dark:focus:ring-primary-500.dark:focus:border-primary-500
                {:type "number"
                 :name "price"
                 :value "2999"
                 :placeholder "$299"
                 :required ""}]]
              [:div
               [:label.block.mb-2.text-sm.font-medium.text-gray-900.dark:text-white
                {:for "category"}
                "Category"]
               [:select#category.bg-gray-50.border.border-gray-300.text-gray-900.text-sm.rounded-lg.focus:ring-primary-500.focus:border-primary-500.block.w-full.p-2.5.dark:bg-gray-700.dark:border-gray-600.dark:placeholder-gray-400.dark:text-white.dark:focus:ring-primary-500.dark:focus:border-primary-500
                (for [option [{:display "Electronics" :v "Electronics" :selected true}
                              {:display "TV/Monitors" :v "TV"}
                              {:display "PC" :v "PC"}
                              {:display "Gaming/Console" :v "GA"}
                              {:display "Phones" :v "PH"}]]
                  [:option {:selected (:selected option false) :value (:v option)}
                   (:display option)])]]
              [:div
               [:label.block.mb-2.text-sm.font-medium.text-gray-900.dark:text-white
                {:for "item-weight"}
                "Item Weight (kg)"]
               [:input#item-weight.bg-gray-50.border.border-gray-300.text-gray-900.text-sm.rounded-lg.focus:ring-primary-600.focus:border-primary-600.block.w-full.p-2.5.dark:bg-gray-700.dark:border-gray-600.dark:placeholder-gray-400.dark:text-white.dark:focus:ring-primary-500.dark:focus:border-primary-500
                {:type "number"
                 :name "item-weight"
                 :value "15"
                 :placeholder "Ex. 12"
                 :required ""}]]
              [:div.sm:col-span-2
               [:label.block.mb-2.text-sm.font-medium.text-gray-900.dark:text-white
                {:for "description"}
                "Description"]
               [:textarea#description.block.p-2.5.w-full.text-sm.text-gray-900.bg-gray-50.rounded-lg.border.border-gray-300.focus:ring-primary-500.focus:border-primary-500.dark:bg-gray-700.dark:border-gray-600.dark:placeholder-gray-400.dark:text-white.dark:focus:ring-primary-500.dark:focus:border-primary-500
                {:rows "8" :placeholder "Write a product description here..."}
                ""]]]
             [:div.flex.items-center.space-x-4
              [:button.bg-amber.hover:bg-blue-800.hover:text-white.focus:ring-4.focus:outline-none.focus:ring-primary-300.font-medium.rounded-lg.text-sm.px-5.py-2.5.text-center.dark:bg-blue-600.dark:hover:bg-blue.dark:focus:ring-primary-800
               {:type "submit"}
               "Update product"]
              [:button.text-red-600.inline-flex.items-center.hover:text-white.border.border-red-600.hover:bg-red-600.focus:ring-4.focus:outline-none.focus:ring-red-300.font-medium.rounded-lg.text-sm.px-5.py-2.5.text-center.dark:border-red-500.dark:text-red-500.dark:hover:text-white.dark:hover:bg-red-600.dark:focus:ring-red-900
               {:type "button"}
               [:svg.w-5.h-5.mr-1.-ml-1 {:fill "currentColor"
                                         :viewBox "0 0 20 20"
                                         :xmlns "http://www.w3.org/2000/svg"}
                [:path
                 {:fill-rule "evenodd"
                  :d
                  "M9 2a1 1 0 00-.894.553L7.382 4H4a1 1 0 000 2v10a2 2 0 002 2h8a2 2 0 002-2V6a1 1 0 100-2h-3.382l-.724-1.447A1 1 0 0011 2H9zM7 8a1 1 0 012 0v6a1 1 0 11-2 0V8zm5-1a1 1 0 00-1 1v6a1 1 0 102 0V8a1 1 0 00-1-1z"
                  :clip-rule "evenodd"}]]
               "Delete"]]]]]}))

(defn page1-hanlder [req]
  (m/match
   req
    {:request-method :post :params {:action "xxx"}}
    (rr/redirect (url req :demo/page1))

    :else
    (render-view (page1-view req))))

(defn page1-id-hanlder [req]
  (def req req)
  {:status 200, :body (url req :demo/page1)})

(defn make-router
  []
  ["" {:middleware (common/middlewares "demo")}
   ["/page1" {:handler #'page1-hanlder :name :demo/page1}]
   ["/page1/:id" {:handler #'page1-id-hanlder :name :demo/page1-id}]])
