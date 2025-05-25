(ns calorias-api.controller
  (:require [calorias-api.db :as db]
            [cheshire.core :as json]
            [calorias-api.services :as svc]
            [ring.util.response :refer [response status]]))

(defn handle-saldo []
  (response {:saldo (db/saldo)}))

(defn handle-transacoes []
  (response (db/transacoes)))

(defn handle-limpar []
  (do
    (db/limpar)
    (response {:mensagem "Transações apagadas."})))


(defn handle-alimento [nome]
  (try
    (let [resultado (svc/buscar-alimento nome)
          transacao (db/registrar {:tipo "alimento"
                                   :nome nome
                                   :valor (:calorias resultado)})]
      {:status 200
       :headers {"Content-Type" "application/json"}
       :body (json/generate-string transacao)})
    (catch Exception _
      {:status 400 :body "Erro ao buscar alimento"})))


(defn handle-exercicio [nome peso tempo altura idade genero]
  (try
    (let [p (Double/parseDouble peso)
          t (Integer/parseInt tempo)
          h (Integer/parseInt altura)
          i (Integer/parseInt idade)
          resultado (svc/buscar-exercicio nome p t h i genero)
          transacao (db/registrar {:tipo "exercicio"
                                   :nome nome
                                   :tempo t
                                   :valor (:gasto resultado)})]
      {:status 200
       :headers {"Content-Type" "application/json"}
       :body (json/generate-string transacao)})
    (catch Exception _
      {:status 400 :body "Erro ao buscar exercício"})))
