(ns utils.core)

;; Note, the contains? in clojure core is not checking for element within seq. it's checking for key exists
;;It does not check whether a collection contains a value; it checks
;;whether an item could be retrieved with get or, in other words,
;;whether a collection contains a key
(defn in? 
  "true if coll contains elm, otherwise nil"
  [coll elm]  
  (some #(= elm %) coll))


