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
  (reduce calcular 0 @registros))