(ns lesson.core                                        
  (:require [io.pedestal.http :as http]          
            [io.pedestal.http.route :as route]
            [hiccup.core :refer [html]]))

(def respond-hello
  {:name :hello
   :enter (fn [ctx]
            (def ctx ctx)
            (assoc ctx :response {:status 200 :html [:html
                                                     [:head]
                                                     [:body [:h1 "sssh"]
                                                      [:div#app]
                                                      [:script {:src "/js/core.js"
                                                                :type "text/javascript"}]]]}))})

(def html-renderer
  {:name  ::html-response
   :leave (fn [{:keys [response]
                :as   ctx}]
            (if (contains? response :html)
              (let [html-body (->> response
                                   :html
                                   html
                                   (str "\n"))]
                (assoc ctx :response (-> response
                                         (assoc :body html-body)
                                         (assoc-in [:headers "Content-Type"] "text/html"))))
              ctx))})




(defn routes
  []
  (route/expand-routes                                   
   #{["/greet" :get [html-renderer respond-hello] :route-name :greet]}))

(defonce server (atom nil))

(defn create-server []
  (http/create-server
   {::http/routes routes
    ::http/type :jetty
    ::http/port 8890
    ::http/join? false
    ::http/resource-path "/public"
    ::http/allowed-origin {:creds true :allowed-origins (constantly true)}
    ::http/secure-headers nil}))

(defn start []
  (when @server
    (http/stop @server))
  (reset! server (-> (create-server)
                     http/default-interceptors
                     http/dev-interceptors
                     http/start)))

(defn stop []
  (http/stop @server)
  (reset! server nil))

(defn reset []
  (stop)
  (start))

(comment (reset))





