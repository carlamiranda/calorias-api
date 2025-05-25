(ns calorias-front.core
  (:gen-class)
  (:require [clj-http.client :as http]
            [cheshire.core :as json]
            [clojure.string :as str]))

(defn buscar-alimento [nome]
  (let [res (http/get "http://localhost:3000/alimento"
                      {:query-params {"nome" nome}
                       :as :json})]
    (:body res)))

(defn buscar-exercicio [nome peso tempo altura idade genero]
  (let [res (http/get "http://localhost:3000/exercicio"
                      {:query-params {"nome" nome
                                      "peso" (str peso)
                                      "tempo" (str tempo)
                                      "altura" (str altura)
                                      "idade" (str idade)
                                      "genero" genero}
                       :as :json})]
    (:body res)))

(defn buscar-saldo []
  (-> (http/get "http://localhost:3000/saldo" {:as :json}) :body :saldo))

(defn buscar-transacoes []
  (-> (http/get "http://localhost:3000/transacoes" {:as :json}) :body))

(defn limpar-transacoes []
  (http/post "http://localhost:3000/limpar")
  (println "Histórico apagado com sucesso."))

(defn registrar [entrada peso altura idade genero]
  (if (str/includes? entrada "min")
    (let [[exercicio tempo-str] (str/split entrada #" ")
          tempo (Integer/parseInt (str/replace tempo-str "min" ""))]
      (try
        (let [gasto (buscar-exercicio exercicio peso tempo altura idade genero)]
          (println (str "Gastou " (int (:valor gasto)) " cal com " (:nome gasto))))
        (catch Exception _e
          (println "Exercício não reconhecido."))))
    (try
      (let [cal (buscar-alimento entrada)]
        (println (str "Ingeriu " (int (:valor cal)) " cal com " (:nome cal))))
      (catch Exception _e
        (println "Alimento não reconhecido.")))))

(defn menu []
  (println "\n--- Menu ---")
  (println "1 - Adicionar alimento ou exercício")
  (println "2 - Ver saldo total")
  (println "3 - Ver histórico de transações")
  (println "4 - Limpar histórico")
  (println "5 - Sair"))

(defn -main [& args]
  (println "Bem-vindo ao Rastreador de Calorias")

  (println "Digite seu peso (kg):")
  (let [peso (Double/parseDouble (read-line))
        _ (println "Digite sua altura (cm):")
        altura (Integer/parseInt (read-line))
        _ (println "Digite sua idade:")
        idade (Integer/parseInt (read-line))
        _ (println "Digite seu gênero (male/female):")
        genero (read-line)]

    (loop []
      (menu)
      (print "Escolha uma opção: ") (flush)
      (let [opcao (read-line)]
        (case opcao
          "1" (do
                (println "Digite alimento ou exercício (ex: banana / running 30min):")
                (let [entrada (read-line)]
                  (registrar entrada peso altura idade genero))
                (recur))
          "2" (do
                (println (str "Saldo atual de calorias: " (buscar-saldo)))
                (recur))
          "3" (do
                (println "Histórico de transações:")
                (doseq [t (buscar-transacoes)]
                  (println t))
                (recur))
          "4" (do
                (limpar-transacoes)
                (recur))
          "5" (do
                (println "Encerrando.")
                (System/exit 0))
          (do
            (println "Opção inválida.")
            (recur)))))))
