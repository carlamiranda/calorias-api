(ns calorias-api.handler
  (:require [compojure.core :refer [defroutes GET POST]]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
            [calorias-api.controller :as ctrl]))

(defroutes rotas
  (GET "/" [] "API de calorias está online.")
  (GET "/alimento" [nome] (ctrl/handle-alimento nome))
  (GET "/exercicio" [nome peso tempo altura idade genero] (ctrl/handle-exercicio nome peso tempo altura idade genero))

  (GET "/transacoes" [] (ctrl/handle-transacoes))
  (GET "/saldo" [] (ctrl/handle-saldo))
  (POST "/limpar" [] (ctrl/handle-limpar))
  (POST "/usuario" request (ctrl/handle-registrar-usuario request))
  (GET "/usuario" [] (ctrl/handle-obter-usuario))

  (route/not-found "404 - Recurso não encontrado"))

(def app
  (-> rotas
      (wrap-json-body {:keywords? true})
      wrap-json-response
      (wrap-defaults api-defaults)))
