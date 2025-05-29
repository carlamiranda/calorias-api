(ns calorias-front.core
  (:gen-class)
  (:require [clj-http.client :as http]
            [cheshire.core :as json]
            [clojure.string :as str]))

(defn buscar-alimento [nome]
  (:body
   (http/get "http://localhost:3000/alimento"
             {:query-params {"nome" nome}
              :as :json})))

(defn buscar-exercicio [nome peso tempo altura idade genero]
  (:body
   (http/get "http://localhost:3000/exercicio"
             {:query-params {"nome" nome
                             "peso" (str peso)
                             "tempo" (str tempo)
                             "altura" (str altura)
                             "idade" (str idade)
                             "genero" genero}
              :as :json})))

(defn buscar-saldo []
  (-> (http/get "http://localhost:3000/saldo" {:as :json}) :body :saldo))

(defn buscar-transacoes []
  (-> (http/get "http://localhost:3000/transacoes" {:as :json}) :body))

(defn limpar-transacoes []
  (http/post "http://localhost:3000/limpar")
  (println "Histórico apagado com sucesso."))

(defn registrar-usuario [altura peso idade genero]
  (http/post "http://localhost:3000/usuario"
             {:body (json/generate-string {:altura altura
                                           :peso peso
                                           :idade idade
                                           :genero genero})
              :headers {"Content-Type" "application/json"}
              :as :json}))

(defn buscar-usuario []
  (:body (http/get "http://localhost:3000/usuario" {:as :json})))

(defn apresentar-usuario [usuario]
  (str "\nUsuário registrado:"
       "\nAltura: " (:altura usuario) " cm"
       "\nPeso: " (:peso usuario) " kg"
       "\nIdade: " (:idade usuario)
       "\nGênero: " (:genero usuario)))
       
(defn mostrar-usuario []
  (let [usuario (buscar-usuario)]
    (println (apresentar-usuario usuario))))

(defn registrar-exercicio [entrada peso altura idade genero]
  (let [[exercicio tempo-str] (str/split entrada #" ")
        tempo (Integer/parseInt (str/replace tempo-str "min" ""))]
    (try
      (let [gasto (buscar-exercicio exercicio peso tempo altura idade genero)]
        (str "Gastou " (int (:valor gasto)) " cal com " (:nome gasto)))
      (catch Exception _
        "Exercício não reconhecido."))))

(defn registrar-alimento [entrada]
  (try
    (let [cal (buscar-alimento entrada)]
      (str "Ingeriu " (int (:valor cal)) " cal com " (:nome cal)))
    (catch Exception _
      "Alimento não reconhecido.")))

(defn registrar [entrada peso altura idade genero]
  (if (str/includes? entrada "min")
    (registrar-exercicio entrada peso altura idade genero)
    (registrar-alimento entrada)))

(defn menu []
  (println "\n--- Menu ---")
  (println "1 - Adicionar alimento ou exercício")
  (println "2 - Ver saldo total")
  (println "3 - Ver histórico de transações")
  (println "4 - Limpar histórico")
  (println "5 - Mostrar usuário registrado")
  (println "6 - Sair"))

(defn opcoes-menu [peso altura idade genero]
  (menu)
  (print "Escolha uma opção: ") (flush)
  (let [opcao (read-line)]
    (case opcao
      "1" (do
            (println "Digite alimento ou exercício (ex: banana / running 30min). Digite 'finalizar' para encerrar:")
            (doall
             (map println
                  (map #(registrar % peso altura idade genero)
                       (take-while #(not (#{"finalizar" "Finalizar"} %))
                                   (repeatedly #(read-line))))))
            (recur peso altura idade genero))
      "2" (do
            (let [dados (buscar-saldo)
                  consumidas (:consumidas dados)
                  gastas (:gastas dados)
                  saldo (:saldo dados)]
              (println (str "\nCalorias consumidas: " consumidas))
              (println (str "Calorias gastas: " gastas))
              (println (str "Saldo de calorias: " saldo)))
            (recur peso altura idade genero))
      "3" (do
            (println "Histórico de transações:")
            (doall (map println (buscar-transacoes)))
            (recur peso altura idade genero))
      "4" (do
            (limpar-transacoes)
            (recur peso altura idade genero))
      "5" (do
            (mostrar-usuario)
            (recur peso altura idade genero))
      "6" (do
            (println "Encerrando.")
            (System/exit 0))
      (do
        (println "Opção inválida.")
        (recur peso altura idade genero)))))

(defn -main [& args]
  (println "Bem-vindo(a) ao Rastreador de Calorias")

  (println "Digite seu peso (kg):")
  (let [peso (Double/parseDouble (read-line))
        _ (println "Digite sua altura (cm):")
        altura (Integer/parseInt (read-line))
        _ (println "Digite sua idade:")
        idade (Integer/parseInt (read-line))
        _ (println "Digite seu gênero (male/female):")
        genero (read-line)]

    (registrar-usuario altura peso idade genero)
    (opcoes-menu peso altura idade genero)))