(ns compare.methods)
(require '[com.ashafa.clutch :as clutch])

(defn open-connection []
  (let [conn (doto
               (clutch/couch "cup_dup")
               (clutch/create!))]
    conn))

(def db-connection (delay (open-connection)))

(defn db [] @db-connection)

(defn load-record
  [id]
  {:body (get-in (db) [id])})

(defn load-all
  []
  {:body
   (map #(second %)
        (seq (db)))
   })

(defn save-record
  [data]
  (let [id (get-in data ["_id"])]
    (clutch/assoc! (db) id data)
    {:body (get-in (db) [id])}))

