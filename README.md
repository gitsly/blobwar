# Blobwar
A minimalistic game written in clojure using quil for graphics.

## Usage
LightTable - open `core.clj` and press `Ctrl+Shift+Enter` to evaluate the file.

Emacs - run cider, open `core.clj` and press `C-c C-k` to evaluate the file.
 or `C-c M-j` to attach with CIDER to a REPL.

  Currently the jack in screws with the namespaces / state of defrecords in
  clojure
  and yields

Exception in  :draw  function:  #error {
 :cause No implementation of method: :update-sys of protocol: #'blobwar.ecs.EcsSystem/EcsSystem found for class: blobwar.systems.drawing.Sys
 :via


I suspect the current version of cider nREPL is causing issue.
I had no problems while running:
 refactor-nrepl 3.6.0
 cider-nrepl 0.29.0

Altough, currently the program runs nice with 'lein run'
To develop, (workaround to get cider joy)
One can start the repl in headless mode
> lein repl :headless

Then connect to it in emacs using: cider-connect
Once in REPL, manually call -main method to start the app.

## License
Copyright © 2023 Martin Collberg
Distributed under the Eclipse Public License either version 2.0
