(ns hi.projects.demo.core
  (:require
   [hiccup2.core :as h]
   [clojure.core.match :as m]
   [ring.util.response :as rr]
   [hi.http.common :as common]
   [hi.utils :refer [url]]))

;; --------------- State -----------------
(def products (atom [{:kind "product" :name "demo1" :price 150 :category "" :id "1"}
                     {:kind "product" :name "demo2" :price 1800 :category "" :id "2"}
                     {:kind "product" :name "demo3" :price 10 :category "" :id "3"}]))


;; ---------------- Actions to update state ----------------
(defn update-product
  [id f]
  (swap! products
         (fn [products]
           (map (fn [product]
                  (if (= id (:id product))
                    (f product)
                    product))
                products))))

(defn delete-product
  [id]
  (swap! products (fn [prods] (filter #(not= (:id %) id) prods))))

(comment
  (delete-product "1")
  (update-product "1" (fn [p] (assoc p :price (* 1.1 (:price p))))))

;; ---------------- Views ----------------
(defn layout
  [{_req :req body :body head :head}]
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
   [:body#body-pd {:class "bg-light"}
    [:div {:class "page"}
     [:div {:class "page-wrapper"}
      [:div {:class "page-body"} [:div {:class "container-xl"} body]]]]]])

;; ---------------- Products ----------------
(defn products-view
  [req]
  (layout
   {:req req
    :head []
    :body [:section.bg-white.dark:bg-gray-900
           [:div.max-w-2xl.px-4.py-8.mx-auto.lg:py-16
            [:h2.mb-4.text-xl.font-bold.text-gray-900.dark:text-white "Products"]
            (for [product @products]
              [:a.block.max-w-sm.p-6.bg-white.border.border-gray-200.rounded-lg.shadow.hover:bg-gray-100.dark:bg-gray-800.dark:border-gray-700.dark:hover:bg-gray-700
               {:href (url req :demo/product {:id (:id product)})}
               [:h5.mb-2.text-2xl.font-bold.tracking-tight.text-gray-900.dark:text-white
                (:name product)]
               [:div.flex.items-center.justify-between
                [:span.text-3xl.font-bold.text-gray-900.dark:text-white (str "Price: " (:price product))]]
               [:p.font-normal.text-gray-700.dark:text-gray-400
                (:category product)]])]]}))

(defn products-hanlder [req]
  (m/match
   req
    {:request-method :post :params {:action "xxx"}}
    (rr/redirect (url req :demo/product))

    :else
    {:status 200 :body (str (h/raw "<!DOCTYPE html>") (h/html (products-view req)))}))

;; ---------------- Product ----------------
(defn product-view
  [req]
  (layout
   {:req req
    :body
    (let [id (get-in req [:path-params :id])
          product (-> (filter (fn [p] (= (:id p) id)) @products)
                      first)
          _ (assert product "Not found")]
      [:section.bg-white.dark:bg-gray-900
       [:div.max-w-2xl.px-4.py-8.mx-auto.lg:py-16
        [:h2.mb-4.text-xl.font-bold.text-gray-900.dark:text-white "Product"]
        [:form#product-form {:method "POST"}
         [:div.grid.gap-4.mb-4.sm:grid-cols-2.sm:gap-6.sm:mb-5
          [:div.sm:col-span-2
           [:label.block.mb-2.text-sm.font-medium.text-gray-900.dark:text-white
            {:for "name"}
            "Product Name"]
           [:input#name.bg-gray-50.border.border-gray-300.text-gray-900.text-sm.rounded-lg.focus:ring-primary-600.focus:border-primary-600.block.w-full.p-2.5.dark:bg-gray-700.dark:border-gray-600.dark:placeholder-gray-400.dark:text-white.dark:focus:ring-primary-500.dark:focus:border-primary-500
            {:type "text"
             :name "name"
             :value (:name product)
             :placeholder "Type product name"
             :required true}]]
          [:div.w-full
           [:label.block.mb-2.text-sm.font-medium.text-gray-900.dark:text-white
            {:for "price"}
            "Price"]
           [:input#price.bg-gray-50.border.border-gray-300.text-gray-900.text-sm.rounded-lg.focus:ring-primary-600.focus:border-primary-600.block.w-full.p-2.5.dark:bg-gray-700.dark:border-gray-600.dark:placeholder-gray-400.dark:text-white.dark:focus:ring-primary-500.dark:focus:border-primary-500
            {:type "number"
             :name "price"
             :value (:price product)
             :placeholder "$$$"}]]
          [:div
           [:label.block.mb-2.text-sm.font-medium.text-gray-900.dark:text-white
            {:for "category"}
            "Category"]
           [:select#category.bg-gray-50.border.border-gray-300.text-gray-900.text-sm.rounded-lg.focus:ring-primary-500.focus:border-primary-500.block.w-full.p-2.5.dark:bg-gray-700.dark:border-gray-600.dark:placeholder-gray-400.dark:text-white.dark:focus:ring-primary-500.dark:focus:border-primary-500
            {:name "category"}
            (for [option [{:display "Electronics" :v "Electronics" :selected true}
                          {:display "TV/Monitors" :v "TV"}
                          {:display "PC" :v "PC"}
                          {:display "Gaming/Console" :v "GA"}
                          {:display "Phones" :v "PH"}]]
              [:option {:selected (or (= (:v option) (:category product)) (:selected option false)) :value (:v option)}
               (:display option)])]]]
         [:div.flex.items-center.space-x-4
          [:button.bg-amber.hover:bg-blue-800.hover:text-white.focus:ring-4.focus:outline-none.focus:ring-primary-300.font-medium.rounded-lg.text-sm.px-5.py-2.5.text-center.dark:bg-blue-600.dark:hover:bg-blue.dark:focus:ring-primary-800
           {:type "submit"
            :formaction (url req :demo/product
                             {:id id}
                             :? (assoc (:query-params req) "action" "update"))}
           "Update product"]
          [:button.text-red-600.inline-flex.items-center.hover:text-white.border.border-red-600.hover:bg-red-600.focus:ring-4.focus:outline-none.focus:ring-red-300.font-medium.rounded-lg.text-sm.px-5.py-2.5.text-center.dark:border-red-500.dark:text-red-500.dark:hover:text-white.dark:hover:bg-red-600.dark:focus:ring-red-900
           {:type "submit"
            :formaction (url req :demo/product
                             {:id id}
                             :? (assoc (:query-params req) "action" "delete"))}
           [:svg.w-5.h-5.mr-1.-ml-1 {:fill "currentColor"
                                     :viewBox "0 0 20 20"
                                     :xmlns "http://www.w3.org/2000/svg"}
            [:path
             {:fill-rule "evenodd"
              :d
              "M9 2a1 1 0 00-.894.553L7.382 4H4a1 1 0 000 2v10a2 2 0 002 2h8a2 2 0 002-2V6a1 1 0 100-2h-3.382l-.724-1.447A1 1 0 0011 2H9zM7 8a1 1 0 012 0v6a1 1 0 11-2 0V8zm5-1a1 1 0 00-1 1v6a1 1 0 102 0V8a1 1 0 00-1-1z"
              :clip-rule "evenodd"}]]
           "Delete"]]]]])}))

(defn product-hanlder [req]
  (m/match
   req
    {:request-method :post :params {:action "update"}}
    (do
      (prn "Call update action")
      (rr/redirect (url req :demo/products)))

    {:request-method :post :params {:action "delete"}}
    (do
      (prn "Call delete action")
      (rr/redirect (url req :demo/products)))

    :else
    {:status 200 :body (str (h/raw "<!DOCTYPE html>") (h/html (product-view req)))}))


;; ---------------- Router ----------------
(defn make-router
  []
  ["" {:middleware (common/middlewares "demo")}
   ["/products" {:handler #'products-hanlder :name :demo/products}]
   ["/products/:id" {:handler #'product-hanlder :name :demo/product}]])
