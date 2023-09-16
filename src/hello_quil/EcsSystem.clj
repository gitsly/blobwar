(ns hello-quil.core)


(defprotocol EcsSystem
  "The protocol for a System. Takes the entire ESC state and performs the systems
  actions upon it. Returns the updated state"
  (update [this state] "Update state in system"))

