(ns calorias-front.core
  (:gen-class)
  (:require [clj-http.client :as client]
            [cheshire.core :as json]))

(def api-url "http://localhost:3000/alimento")

(defn buscar-alimento [nome]
  (let [response (client/get api-url
                             {:query-params {"nome" nome}
                              :as :json})]
    (:body response)))

(defn -main [& args]
  (println "Digite o nome do alimento:")
  (let [nome (read-line)
        resultado (buscar-alimento nome)]
    (println "Resultado para:" nome)
    (println (json/generate-string resultado {:pretty true}))))
