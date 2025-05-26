(ns calorias-api.db)

(def registros (atom[]))

(defn transacoes [] @registros)

(defn limpar [] (reset! registros[]))

(defn registrar [transacao]
  (let [colecao-atualizada (swap! registros conj transacao)]
    (merge transacao {:id (count colecao-atualizada)})))

(defn- exercicio? [transacao]
 (= (:tipo transacao) "exercicio"))

(defn- calcular [acumulado transacao]
  (let [valor (:valor transacao)]
    (if (exercicio? transacao)
      (- acumulado valor)
      (+ acumulado valor))))

(defn saldo []
  (let [registros @registros
        consumidas (reduce + 0 (map :valor (remove exercicio? registros)))
        gastas    (reduce + 0 (map :valor (filter exercicio? registros)))
        saldo     (- consumidas gastas)]
    {:consumidas consumidas
     :gastas gastas
     :saldo saldo}))