(ns quick-start)

;; == Some VS Code knowledge required ==
;; This tutorial assumes you know a few things about
;; VS Code. Please check out this page if you are new
;; to the editor: https://code.visualstudio.com/docs
;;
;; == Keyboard Shortcuts Notation used in this tutorial ==
;; We use a notation for keyboard shortcuts, where
;; `+` means the keys are pressed at the same time
;; and ` ` separates any keyboard presses in the sequence.
;; `Ctrl+Alt+C Enter` means to press Ctrl, Alt, and C
;; all at the same time, then release the keys and
;; then press Enter. (The Alt key is named Optuion or
;; Opt, on some machines)

"Welcome to the Getting Started REPL! 💜"

;; == You Control what is Evaluated ==
;; Please note that Calva never evaluates your code
;; unless you explicitly ask for it. So, except for
;; this file, you will have to load files you open
;; yourself. Make it a habit to do this, because
;; sometimes things don't work, and they fail in
;; peculiar ways, when your file is not loaded.

;; Try it with this file: `Ctrl+Alt+C Enter`.
;; The result of loading a file is whatever is the
;; last top level form in the file.

;; Once you see a message in the output/REPL window ->
;; saying that this file is loaded, you can start by
;; placing the cursor anywhere on line 17 and press
;; `Alt+Enter`. (`Option+Enter` on some machines.)

;; Did it? Great!
;; See that `=> "Welcome ...` at the end of the line?
;; That's the result of the evaluation you just
;; performed. You just used the Clojure REPL!
;; 🎉 Congratulations! 🎂

(comment
  ;; You can evaluate the string below in the same way

  "Hello World!"
  
  ;; Evaluate the following form too (you can
  ;; place the cursor anywhere on any of the two lines):

  (repeat 7
          "I am using the REPL! 💪")

  ;; Only `=> ("I am using the REPL! 💪"` is displayed
  ;; inline. You can see the full result, and also copy
  ;; it, if you hover the evaluated expression. Or press
  ;; `Ctrl+K Ctrl/Cmd+I`.

  ;; Let's get into the mood for real. 😂
  ;; Place the cursor on any of the five code lines below:
  ;; `Alt+Enter`, then `Cmd+K Cmd+I`.

  (map (fn [s]
         (if (< (count s) 5)
           (str "Give me " s "! ~•~ " (last s) "!")
           s))
       ["an R" "an E" "a  P" "an L" "What do you get?" "REPL!"])
  
  ;; Clear the inline display with `Esc`. The inline
  ;; results are also cleared when you edit the file.
  )
