(ns net.cgrand.enlive-html)

(defmacro deftemplate
 "Defines a template as a function that returns a seq of strings."
 [name _source args & forms]
  `(def ~name (fn [~@args] ~@forms)))

(defmacro defsnippet
 "Define a named snippet -- equivalent to (def name (snippet source selector args ...))."
 [name _source _selector args & forms]
 `(def ~name (fn  [~@args] ~@forms)))
