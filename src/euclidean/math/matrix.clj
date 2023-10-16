(ns euclidean.math.matrix
  (:require [euclidean.math.vector :as vec])
  (:import (euclidean.math.vector Vector2D Vector3D)))

(defprotocol Matrix
  (^:no-doc add* [m1 m2] "Add two matrices together.")
  (^:no-doc sub* [m1 m2] "Subtract the second matrix from the first.")
  (^:no-doc mult* [m1 m2] "Multiply one matrix by another.")
  (negate [m] "Negates each component of the input matrix.")
  (invert [m] "Inverts the input matrix.")
  (scale [m s] "Scales the matrix")
  (^double determinant [m] "Returns the determinant of the input matrix.")
  (transpose [m] "Returns the transpose of the input matrix."))

(defprotocol TransformVector
  (transform [m1 v1] "Multiply one vector by matrix."))

(defprotocol AdditiveIdentity
  (add-identity [x] "Returns the additive identity of the input."))

(defprotocol MultiplicativeIdentity
  (mult-identity [x] "Returns the multiplicative identity of the input."))

(defprotocol TranslateBy
  (translate [m v] "Translate the matrix by the given vector."))

(defmacro ^:private hash-matrix-fields
  [& fields]
  `(-> (int 1)
       ~@(interleave (repeat (count fields) `(unchecked-multiply-int 31))
                     (for [field fields]
                       `(unchecked-add-int
                         (if (zero? ~field)
                           0
                           (Float/floatToIntBits ~field)))))))

(declare identity-mat2 identity-mat3 identity-mat4)

(deftype Matrix2D [^double m00 ^double m01
                   ^double m10 ^double m11]
  clojure.lang.Counted
  (count [_] 2)

  clojure.lang.Sequential

  clojure.lang.Seqable
  (seq [_] (list (Vector2D. m00 m01) (Vector2D. m10 m11)))

  clojure.lang.ILookup
  (valAt [m i]
    (.valAt m i nil))
  (valAt [_ i not-found]
    (case (int i)
      0 (Vector2D. m00 m01)
      1 (Vector2D. m10 m11)
      not-found))

  clojure.lang.Associative
  (equiv [m o]
    (.equals m o))
  (containsKey [_ k]
    (case (int k)
      (0 1) true
      false))
  (entryAt [m k]
    (case (int k)
      0 (clojure.lang.MapEntry. 0 (.valAt m 0))
      1 (clojure.lang.MapEntry. 1 (.valAt m 1))))
  (assoc [m i [x y]]
    (case (int i)
      0 (Matrix2D. x y m10 m11)
      1 (Matrix2D. m00 m01 x y)))

  clojure.lang.IFn
  (invoke [m i]
    (.valAt m i))

  Object
  (toString [_]
    (str "#math/matrix [" [m00  m01] " " [m10 m11] "]"))
  (hashCode [_]
    (hash-matrix-fields m00 m01 m10 m11))
  (equals [self m]
    (or (identical? self m)
        (and (instance? Matrix2D m)
             (== (hash self) (hash m)))
        (and (counted? m)
             (== (count m) 2)
             (= (Vector2D. m00 m01) (m 0))
             (= (Vector2D. m10 m11) (m 1)))))

  Matrix
  (add* [_ m2]
    (let [m2 ^Matrix2D m2]
      (Matrix2D. (+ m00 (.-m00 m2)) (+ m01 (.-m01 m2))
                 (+ m10 (.-m10 m2)) (+ m11 (.-m11 m2)))))
  (sub* [_ m2]
    (let [m2 ^Matrix2D m2]
      (Matrix2D. (- m00 (.-m00 m2)) (- m01 (.-m01 m2))
                 (- m10 (.-m10 m2)) (- m11 (.-m11 m2)))))
  (mult* [_ m2]
    (let [m2 ^Matrix2D m2]
      (Matrix2D. (+ (* m00 (.-m00 m2)) (* m01 (.-m10 m2)))
                 (+ (* m00 (.-m01 m2)) (* m01 (.-m11 m2)))
                 (+ (* m10 (.-m00 m2)) (* m11 (.-m10 m2)))
                 (+ (* m10 (.-m01 m2)) (* m11 (.-m11 m2))))))
  (negate [_]
    (Matrix2D. (- m00) (- m01) (- m10) (- m11)))
  (invert [m]
    (let [det (determinant m)]
      (when-not (zero? det)
        (let [det-inv (/ det)]
          (Matrix2D. (* m11 det-inv) (- (* m01 det-inv))
                     (- (* m10 det-inv)) (* m00 det-inv))))))

  (scale [m2 s]
    ;; m00 *= sx;  m01 *= sy;
    ;; m10 *= sx;  m11 *= sy;
    (let [m2 ^Matrix2D m2
          sx s
          sy s]
      (Matrix2D. (* sx (.-m00 m2)) (* sy (.-m01 m2))
                 (* sx (.-m10 m2)) (* sy (.-m11 m2)))))

  (determinant [_]
    (- (* m00 m11) (* m10 m01)))
  (transpose [_]
    (Matrix2D. m00 m10 m01 m11)))

;; PMatrix from processing
;; [m00 m01 m02]
;; [m10 m11 m12]
;; [ 0   0   1 ]
(deftype Matrix3D [^double m00 ^double m01 ^double m02
                   ^double m10 ^double m11 ^double m12
                   ^double m20 ^double m21 ^double m22]
  clojure.lang.Counted
  (count [_] 3)

  clojure.lang.Sequential

  clojure.lang.Seqable
  (seq [_] (list (Vector3D. m00 m01 m02)
                 (Vector3D. m10 m11 m12)
                 (Vector3D. m20 m21 m22)))

  clojure.lang.ILookup
  (valAt [m i]
    (.valAt m i nil))
  (valAt [_ i not-found]
    (case (int i)
      0 (Vector3D. m00 m01 m02)
      1 (Vector3D. m10 m11 m12)
      2 (Vector3D. m20 m21 m22)
      not-found))

  clojure.lang.Associative
  (equiv [m o] (.equals m o))
  (containsKey [_ k]
    (case (int k)
      (0 1 2) true
      false))
  (entryAt [m k]
    (case (int k)
      0 (clojure.lang.MapEntry. 0 (.valAt m 0))
      1 (clojure.lang.MapEntry. 1 (.valAt m 1))
      2 (clojure.lang.MapEntry. 2 (.valAt m 2))))
  (assoc [m i [x y z]]
    (case (int i)
      0 (Matrix3D. x y z m10 m11 m12 m20 m21 m22)
      1 (Matrix3D. m00 m01 m02 x y z m20 m21 m22)
      2 (Matrix3D. m00 m01 m02 m10 m11 m12 x y z)))

  clojure.lang.IFn
  (invoke [m i]
    (.valAt m i))

  Object
  (toString [_]
    (str "#math/matrix ["
         [m00 m01 m02] " "
         [m10 m11 m12] " "
         [m20 m21 m22] "]"))
  (hashCode [this]
    (hash-matrix-fields m00 m01 m02 m10 m11 m12 m20 m21 m22))
  (equals [self m]
    (or (identical? self m)
        (and (instance? Matrix3D m)
             (== (hash self) (hash m)))
        (and (counted? m)
             (== (count m) 3)
             (= (Vector3D. m00 m01 m02) (m 0))
             (= (Vector3D. m10 m11 m12) (m 1))
             (= (Vector3D. m20 m21 m22) (m 2)))))

  Matrix
  (add* [m1 m2]
    (let [m2 ^Matrix3D m2]
      (Matrix3D. (+ m00 (.-m00 m2)) (+ m01 (.-m01 m2)) (+ m02 (.-m02 m2))
                 (+ m10 (.-m10 m2)) (+ m11 (.-m11 m2)) (+ m12 (.-m12 m2))
                 (+ m20 (.-m20 m2)) (+ m21 (.-m21 m2)) (+ m22 (.-m22 m2)))))
  (sub* [m1 m2]
    (let [m2 ^Matrix3D m2]
      (Matrix3D. (- m00 (.-m00 m2)) (- m01 (.-m01 m2)) (- m02 (.-m02 m2))
                 (- m10 (.-m10 m2)) (- m11 (.-m11 m2)) (- m12 (.-m12 m2))
                 (- m20 (.-m20 m2)) (- m21 (.-m21 m2)) (- m22 (.-m22 m2)))))
  (mult* [m1 m2]
    (let [m2 ^Matrix3D m2]
      (Matrix3D. (+ (* m00 (.-m00 m2)) (* m01 (.-m10 m2)) (* m02 (.-m20 m2)))
                 (+ (* m00 (.-m01 m2)) (* m01 (.-m11 m2)) (* m02 (.-m21 m2)))
                 (+ (* m00 (.-m02 m2)) (* m01 (.-m12 m2)) (* m02 (.-m22 m2)))

                 (+ (* m10 (.-m00 m2)) (* m11 (.-m10 m2)) (* m12 (.-m20 m2)))
                 (+ (* m10 (.-m01 m2)) (* m11 (.-m11 m2)) (* m12 (.-m21 m2)))
                 (+ (* m10 (.-m02 m2)) (* m11 (.-m12 m2)) (* m12 (.-m22 m2)))
                 
                 (+ (* m20 (.-m00 m2)) (* m21 (.-m10 m2)) (* m22 (.-m20 m2)))
                 (+ (* m20 (.-m01 m2)) (* m21 (.-m11 m2)) (* m22 (.-m21 m2)))
                 (+ (* m20 (.-m02 m2)) (* m21 (.-m12 m2)) (* m22 (.-m22 m2))))))


  (negate [_]
    (Matrix3D. (- m00) (- m01) (- m02)
               (- m10) (- m11) (- m12)
               (- m20) (- m21) (- m22)))
  (invert [m]
    (let [det (determinant m)]
      (when-not (zero? det)
        (let [det-inv (/ det)]
          (Matrix3D. (* (- (* m11 m22) (* m21 m12)) det-inv)
                     (* (+ (* (- m01) m22) (* m21 m02)) det-inv)
                     (* (- (* m01 m12) (* m11 m02)) det-inv)
                     
                     (* (+ (* (- m10) m22) (* m20 m12)) det-inv)
                     (* (- (* m00 m22) (* m20 m02)) det-inv)
                     (* (+ (* (- m00) m12) (* m10 m02)) det-inv)
                     
                     (* (- (* m10 m21) (* m20 m11)) det-inv)
                     (* (+ (* (- m00) m21) (* m20 m01)) det-inv)
                     (* (- (* m00 m11) (* m10 m01)) det-inv))))))
  (determinant [_]
    (+ (* m00 (- (* m11 m22) (* m21 m12)))
       (* m10 (- (* m21 m02) (* m01 m22)))
       (* m20 (- (* m01 m12) (* m11 m02)))))
  (transpose [_]
    (Matrix3D. m00 m10 m20 m01 m11 m21 m02 m12 m22))

  (scale [m s]
    (let [sx s
          sy s
          sz s]
      (Matrix3D. (* sx (.-m00 m)) (* sy (.-m01 m)) (.-m02 m)
                 (* sx (.-m10 m)) (* sy (.-m11 m)) (.-m12 m)
                 (* sx (.-m20 m)) (* sy (.-m21 m)) (.-m22 m)
                 ))
    )
  )

(definline det3
  [m00# m10# m20# m01# m11# m21# m02# m12# m22#]
  `(+ (* ~m00# (- (* ~m11# ~m22#) (* ~m21# ~m12#)))
      (* ~m10# (- (* ~m21# ~m02#) (* ~m01# ~m22#)))
      (* ~m20# (- (* ~m01# ~m12#) (* ~m11# ~m02#)))))


(alter-meta! #'->Matrix2D assoc :no-doc true)
(alter-meta! #'->Matrix3D assoc :no-doc true)

(def ^:private identity-mat2
  (Matrix2D. 1.0 0.0 0.0 1.0))

(def ^:private identity-mat3
  (Matrix3D. 1.0 0.0 0.0 0.0 1.0 0.0 0.0 0.0 1.0))

(extend-type Matrix2D
  AdditiveIdentity
  (add-identity [_] (Matrix2D. 0.0 0.0 0.0 0.0))
  MultiplicativeIdentity
  (mult-identity [_] identity-mat2))

(extend-type Matrix3D
  AdditiveIdentity
  (add-identity [_] (Matrix3D. 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0))
  MultiplicativeIdentity
  (mult-identity [_] identity-mat3)

  TranslateBy
  (translate [^Matrix3D m ^Vector2D v]
    "Translate the matrix by the given vector."
    (let [[tx ty] v]
      ;;    [m00 m01 m02]
      ;;    [m10 m11 m12]      ;; m02 = tx*m00 + ty*m01 + m02;
      ;;    [ 0   0   1 ]      ;; m12 = tx*m10 + ty*m11 + m12;
      (Matrix3D. (.-m00 m) (.-m01 m) (+ (* tx (.-m00 m)) (* ty (.-m01 m)) (.-m02 m))
                 (.-m10 m) (.-m11 m) (+ (* tx (.-m10 m)) (* ty (.-m11 m)) (.-m12 m))
                 (.-m20 m) (.-m21 m) (.-m22 m)
                 )))

  TransformVector
  (transform [^Matrix3D m ^Vector2D v ]
    ;; (str "m1:" (type m1) "v1:" (type v1))
    ;;    3x2 affine matrix implementation. Matrices are used to
    ;;    describe a transformation; see PMatrix for a general
    ;;    description. This matrix looks like the following when
    ;;    multiplying a vector (x, y) in mult().
    ;;----------------------------------------------------------
    ;;    [m00 m01 m02][x]   [m00*x + m01*y + m02*1]   [x']
    ;;    [m10 m11 m12][y] = [m10*x + m11*y + m12*1] = [y']
    ;;    [ 0   0   1 ][1]   [ 0*x  +  0*y  +  1*1 ]   [ 1]
    (Vector2D.
     ;; calculate X
     (+ (* (vec/.getX v) (.m00 m))
        (* (vec/.getX v) (.m10 m))
        (.m02 m))
     ;; calculate Y
     (+ (* (vec/.getY v) (.m01 m))
        (* (vec/.getY v) (.m11 m))
        (.m12 m)))))

(defn add
  "Return the sum of one or more matrixs."
  ([m] m)
  ([m1 m2] (add* m1 m2))
  ([m1 m2 & more] (reduce add* (add* m1 m2) more)))

(defn sub
  "If only one matrix is supplied, return the negation of the matrix. Otherwise
  all subsequent matrixs are subtracted from the first."
  ([m] (negate m))
  ([m1 m2] (sub* m1 m2))
  ([m1 m2 & more] (reduce sub* (sub* m1 m2) more)))

(defn mult
  "Performs matrix multiplication on the input matrices."
  ([m] m)
  ([m1 m2] (mult* m1 m2))
  ([m1 m2 & more] (reduce mult* (mult* m1 m2) more)))


(defn mat2
  "Creates a new Matrix2D."
  ([] identity-mat2)
  ([m] (apply mat2 m))
  ([v1 v2]
   (Matrix2D. (v1 0) (v1 1) (v2 0) (v2 1)))
  ([m00 m01 m10 m11]
   (Matrix2D. m00 m01 m10 m11)))

(defn mat3
  "Creates a new Matrix3D."
  ([] identity-mat3)
  ([m] (apply mat3 m))
  ([v1 v2 v3]
   (Matrix3D. (v1 0) (v1 1) (v1 2)
              (v2 0) (v2 1) (v2 2)
              (v3 0) (v3 1) (v3 2))))

(defn matrix
  "Creates a new 2D, 3D, or 4D matrix."
  ([v1 v2] (mat2 v1 v2))
  ([v1 v2 v3] (mat3 v1 v2 v3))
  ([v1 v2 v3 v4])

  ;; PMatrix from processing (Create from float array)
  ;; [m00 m01 m02]
  ;; [m10 m11 m12]
  ;; [ 0   0   1 ]

  ;; Matrix from float vector
  ([[m00 m01 m02 m10 m11 m12]]
   (Matrix3D. m00 m01 m02 m10 m11 m12 0 0 1)))


(defn into-matrix [coll]
  "Turn a collection of numbers into a math matrix."
  (if (satisfies? Matrix coll)
    coll
    (apply matrix coll)))

(defmethod print-method Matrix2D [^Matrix2D v ^java.io.Writer w]
  (.write w (.toString v)))

(defmethod print-method Matrix3D [^Matrix3D v ^java.io.Writer w]
  (.write w (.toString v)))

(defmethod print-dup Matrix2D [^Matrix2D v ^java.io.Writer w]
  (.write w (.toString v)))

(defmethod print-dup Matrix3D [^Matrix3D v ^java.io.Writer w]
  (.write w (.toString v)))
