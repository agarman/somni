(ns somni.middleware.to-ring)

(defn- status?
  [{:keys [status]}]
  (or (nil? status)
      (and (number? status)
           (>= status 100)
           (<= status 599))))

(defn- headers?
  [{:keys [headers]}]
  (or (nil? headers)
      (and (map? headers)
           (every? string? (keys headers)))))

(defn- body?
  [{:keys [status body]}]
  (or body
      (#{204 404} status)))

(defn- response?
  [response]
  (and (map? response)
       (every? identity [(status?  response)
                         (headers? response)
                         (body?    response)])))

(defn- ->response
  [status body]
  {:status status :body body})

(defmulti  response-status
  (fn [{:as req :keys [request-method]} resp] request-method))

(defmethod response-status :get     [_ resp] (if resp 200 404))

(defmethod response-status :default [_ resp] (if resp 200 204))

(defn wrap-response-as-ring
  "If handler returns a ring response or an exception, pass it through.
  Otherwise pack result in a ring response."
  [handler]
  (fn [{:as req :keys [request-method]}]
    (let [resp (handler req)]
      (cond
        (response? resp)            resp
        (instance? Throwable resp) (throw resp)
        :else                      (->response
                                    (response-status req resp)
                                    resp)))))
