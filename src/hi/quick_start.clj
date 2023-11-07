(ns hi.quick-start)

;; Command: Calva: Load/Evaluate current file (or Right click check the menu)
;; Remember the shortcut~~

"Welcome to the Getting Started REPL! 💜"
;; ↑ move your cursor to the begining of line 5
;; and Command: Calva: Evaluate current form (or Right click check the menu)
;; Remember the shortcut~~

;; () [] <-- always pair

;;    name   params         body
;;    -----  ------  -------------------
(defn greet  [name]  (str "Hello, " name) )

;; how to invoke
(greet "students")

;; in js
;; const greet = (name) => `Hello, ${name}`;
;; greet("students")

;; in C#
;; Action<string> greet = name =>
;; {
;;     string greeting = $"Hello, {name}!";
;; };
;; Console.WriteLine(greet("students"));

;; in PHP
;; $greet = function ($name) {
;;     printf ("Hello, %s", $name);
;; };
;; $greet('students');

(comment
  ;; Try
  (assoc {:hello "you"} :hello "world")

  ;; Javascript version:

  ;; const obj = { hello: "you" };
  ;; obj.hello = "world";



  (filter #(not= % 3) [1 2 3 4 5])
  ;; Javascript version:
  ;; [1, 2, 3, 4, 5].filter(x => x !== 3);

;;↓ move your cursor here and Calva: Evaluate current form (or Right click check the menu)
  (let [variable "your definition"
        another-variable "your definition2"]
    ;; or inside the let form and Calva: Evaluate top level form (or Right click check the menu)
    (prn variable)
    (prn another-variable)))
    ;; Javascript version:

    ;; const variable = "your definition";
    ;; const anotherVariable = "your definition2";
    ;; console.log(variable);
    ;; console.log(anotherVariable);



"It's time to conquer new realms and seek out new peaks. ⚡"
