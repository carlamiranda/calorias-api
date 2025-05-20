(ns calorias-front.core
  (:gen-class)
  (:require [clj-http.client :as http]
            [cheshire.core :as json]
            [clojure.string :as str]))

(def acumulador (atom 0))

(defn buscar-alimento [nome]
  (let [res (http/get "http://localhost:3000/alimento"
                      {:query-params {"nome" nome}
                       :as :json})]
    (-> res :body :calorias)))

(defn buscar-exercicio [nome peso tempo altura idade genero]
  (let [res (http/get "http://localhost:3000/exercicio"
                      {:query-params {"nome" nome
                                      "peso" (str peso)
                                      "tempo" (str tempo)
                                      "altura" (str altura)
                                      "idade" (str idade)
                                      "genero" genero}
                       :as :json})]
    (-> res :body :gasto)))

(defn registrar [entrada peso altura idade genero]
  (if (str/includes? entrada "min")
    (let [[exercicio tempo-str] (str/split entrada #" ")
          tempo (Integer/parseInt (str/replace tempo-str "min" ""))]
      (try
        (let [gasto (buscar-exercicio exercicio peso tempo altura idade genero)]
          (swap! acumulador - gasto)
          (println (str "Gastou " (int gasto) " cal com " exercicio)))
        (catch Exception _e
          (println "Exercício não reconhecido."))))
    (try
      (let [cal (buscar-alimento entrada)]
        (swap! acumulador + cal)
        (println (str "Ingeriu " (int cal) " cal com " entrada)))
      (catch Exception _e
        (println "Alimento não reconhecido.")))))

(defn -main [& args]
  (println "📊 Rastreador de Calorias")

  (println "Digite seu peso (kg):")
  (let [peso (Double/parseDouble (read-line))

        _ (println "Digite sua altura (cm):")
        altura (Integer/parseInt (read-line))

        _ (println "Digite sua idade:")
        idade (Integer/parseInt (read-line))

        _ (println "Digite seu gênero (male/female):")
        genero (read-line)]

    (loop []
      (println "\nDigite alimento ou exercício (ex: banana / running 30min) ou 'fim':")
      (let [entrada (read-line)]
        (if (= entrada "fim")
          (do
            (println "\nSaldo final de calorias: " @acumulador)
            (System/exit 0))
          (do
            (registrar entrada peso altura idade genero)
            (recur)))))))
