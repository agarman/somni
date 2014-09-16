(ns carelogistics.perf-tests
  (:require [bidi.bidi :refer :all]
            [carelogistics.somni :as somni]
            [clojure.test :refer :all]
            [compojure.core :refer (GET routes)]
            [ring.mock.request :refer (request)]))

(def ^:dynamic *cnt* 1000000)

(def uris ["/index.html" "/a.html" "/b.html" "/c.html" "/d.html" "/e.html"
           "/blog/f.html" "/blog/g.html" "/blog/h.html" "/blog/i.html"
           "/gallery/j.html" "/gallery/k.html" "/gallery/l.html"
           "/sites/m.html" "/sites/n.html" "/sites/o.html" "/sites/p.html"
           "/m/q.html" "/m/r.html" "/m/s.html" "/m/t.html" "/m/u.html"
           "/foo/bar/v.html" "/foo/baz/w.html" "/foo/quux/x.html"
           "/lambda/y.html" "/lambda/z.html" "/lambda/a.html"])

(def reqs (map (partial request :get) uris))

(defn e [req] {:status 200 :body "e"})

(deftest compojure-control-test []
  (let [ctx (routes
             (GET "/index.html" [] e)
             (GET "/a.html" [] e)
             (GET "/b.html" [] e)
             (GET "/c.html" [] e)
             (GET "/d.html" [] e)
             (GET "/e.html" [] e)
             (GET "/blog/f.html" [] e)
             (GET "/blog/g.html" [] e)
             (GET "/blog/h.html" [] e)
             (GET "/blog/i.html" [] e)
             (GET "/gallery/j.html" [] e)
             (GET "/gallery/k.html" [] e)
             (GET "/gallery/l.html" [] e)
             (GET "/sites/m.html" [] e)
             (GET "/sites/n.html" [] e)
             (GET "/sites/o.html" [] e)
             (GET "/sites/p.html" [] e)
             (GET "/m/q.html" [] e)
             (GET "/m/r.html" [] e)
             (GET "/m/s.html" [] e)
             (GET "/m/t.html" [] e)
             (GET "/m/u.html" [] e)
             (GET "/foo/bar/v.html" [] e)
             (GET "/foo/baz/w.html" [] e)
             (GET "/foo/quux/x.html" [] e)
             (GET "/lambda/y.html" [] e)
             (GET "/lambda/z.html" [] e)
             (GET "/lambda/a.html" [] e))]

    (is (= (ctx (rand-nth reqs)) {:status 200, :headers {}, :body "e"}))
    (println (format "Time for %d matches using Compojure routes" *cnt*))
    (time (dotimes [_ *cnt*] (ctx (rand-nth reqs))))))

(deftest somni-test []
  (let [h (somni/make-handler
           [{:uris uris :handler :x}]
           {:x e}
           {})]

    (is (= (:body (h (rand-nth reqs))) "e"))
    (println (format "Time for %d matches using somni routes" *cnt*))
    (time (dotimes [_ *cnt*] (h (rand-nth reqs))))))
