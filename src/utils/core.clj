(ns utils.core
  (:require [clojure.set :as set]))

;; Note, the contains? in clojure core is not checking for element within seq. it's checking for key exists
;;It does not check whether a collection contains a value; it checks
;;whether an item could be retrieved with get or, in other words,
;;whether a collection contains a key
(defn in? 
  "true if coll contains elm, otherwise nil"
  [coll elm]  
  (some #(= elm %) coll))

(defn except
  "returns immutable vector with all elements except provided ones in
  'except-seq' param, removes duplicated entries as well due to use of
  'set' in underlying implementation. Checks excepted elements for a single element and
  also works with this parameter setup"
  [coll
   exc]
  (let [oth (if (seq? exc)
              exc
              [exc])]
    (into (vector)
          (set/difference (set coll) oth))))
;; Sample usage
;;(except [1 2 3] [2])
;;(except [1 2 3] 2)




