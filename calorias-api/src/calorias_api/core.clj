(ns calorias-api.core
  (:gen class)
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.reload :refer [wrap-reload]]
            [calorias-api.handler :refer [app]]))

(defn -main [& args]
  (println "Iniciando servidor de calorias na orta 3000....")
  (run-jetty (wrap-reload #'app)
             {:port 3000 :join? true}))