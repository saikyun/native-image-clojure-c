(ns start-sci-nrepl
  (:require [sci.core :as sci]
            [sci.addons :as addons]
            [babashka.nrepl.server :as bserv]))

(defn start!
  [stuff]
  (def opts (-> (merge {:namespaces {'foo.bar {'x 1}}}
                       stuff) addons/future))
  (def sci-ctx (sci/init opts))
  (let [host-opts {:host "127.0.0.1" :port 6666}]
    (bserv/start-server! sci-ctx host-opts)
    
    (println "server started!" host-opts)))
