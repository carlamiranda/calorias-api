(ns calorias-api.handler
  (:require [compojure.core :refer [defroutes GET POST]]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [calorias-api.controller :as ctrl]))

(defroutes rotas
  (GET "/" [] "API de calorias está online.")

  (GET "/alimento" [nome]
    (ctrl/handle-alimento nome))

  (GET "/exercicio" [nome peso tempo altura idade genero]
    (ctrl/handle-exercicio nome peso tempo altura idade genero))


  (route/not-found "404 - Recurso não encontrado"))
