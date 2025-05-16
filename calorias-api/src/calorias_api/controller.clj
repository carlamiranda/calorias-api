(ns calorias-api.controller
  (:require [cheshire.core :as json]
            [calorias-api.services :as svc]))

(defn handle-alimento [nome]
  (try
    {:status 200
     :headers {"Content-Type" "application/json"}
     :body (json/generate-string (svc/buscar-alimento nome))}
    (catch Exception _
      {:status 400 :body "Erro ao buscar alimento"})))

(defn handle-exercicio [nome peso tempo]
  (try
    (let [p (Double/parseDouble peso)
          t (Integer/parseInt tempo)]
      {:status 200
       :headers {"Content-Type" "application/json"}
       :body (json/generate-string (svc/buscar-exercicio nome p t))})
    (catch Exception _
      {:status 400 :body "Erro ao buscar exercício"})))