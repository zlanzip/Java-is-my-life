## 服务器端 REST API 常见错误码

REST 接口调用成功时返回 HTTP 状态码为 200，返回数据结果为标准 JSON 格式。如调用错误会返回除 200 之外的其他 HTTP 状态码，返回数据结果也为标准 JSON 格式，可根据返回数据中的 error 字段判断具体错误。

建议对 APP 自己的服务器端调用的环信 REST API 结果做容错处理。比如要 catch 接口调用返回的异常，对于 timeout 这样的错误应该做重试。对于系统级别错误或重试后仍旧出错，应该记录到系统日志，并及时报警提示运维人员做补救措施，如人工补发。

    HTTP 状态返回代码 4xx（请求错误）这些状态代码表示请求可能出错，妨碍了服务器的处理。
    HTTP 状态返回代码 5xx（服务器错误）这些状态代码表示服务器在尝试处理请求时发生内部错误。

HTTP 返回码（Status Code）	说明（Description）

    400	（错误请求）服务器不理解请求的语法。
    401	（未授权）请求要求身份验证。对于需要token的接口，服务器可能返回此响应。
    403	（禁止）服务器拒绝请求。对于群组/聊天室服务，表示本次调用不符合群组/聊天室操作的正确逻辑，例如调用添加成员接口，添加已经在群组里的用户，或者移除聊天室中不存在的成员等操作。
    404	（未找到）服务器找不到请求的接口。
    408	（请求超时）服务器等候请求时发生超时。
    413	（请求体过大）请求体超过了5kb，拆成更小的请求体重试即可。
    415	请求体的类型不支持。
    429	（服务不可用）请求接口超过调用频率限制，即接口被限流。或超过社区版限制，如有需要可联系商务。
    500	（服务器内部错误）服务器遇到错误，无法完成请求。
    501	（尚未实施）服务器不具备完成请求的功能。例如，服务器无法识别请求方法时可能会返回此代码。
    502	（错误网关）服务器作为网关或代理，从上游服务器收到无效响应。
    503	（服务不可用）请求接口超过调用频率限制，即接口被限流。
    504	（网关超时）服务器作为网关或代理，但是没有及时从上游服务器收到请求。