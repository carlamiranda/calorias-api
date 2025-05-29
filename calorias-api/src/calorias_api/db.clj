(ns calorias-api.db)

(def registros (atom []))

(def usuario (atom nil))

(defn transacoes [] @registros)

(defn limpar [] (reset! registros[]))

(defn registrar-usuario [dados]
  (reset! usuario dados))

(defn obter-usuario []
  @usuario)

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

(defn transacoes-por-periodo [inicio fim]
  (filter (fn [t]
            (let [data (:data t)]
              (and (not (neg? (compare data inicio)))
                   (not (pos? (compare data fim))))))
          @registros))

(defn saldo-por-periodo [inicio fim]
  (let [transacoes (transacoes-por-periodo inicio fim)
        consumidas (reduce + 0 (map :valor (remove exercicio? transacoes)))
        gastas (reduce + 0 (map :valor (filter exercicio? transacoes)))]
    {:consumidas consumidas
     :gastas gastas
     :saldo (- consumidas gastas)}))