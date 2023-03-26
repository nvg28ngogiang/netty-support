# Netty Support Project

## Expectation

Provide some supports for Netty WebSocket, such as:
* Contracting template for a client's request
* Mapping request to corresponding handler method using request url provided by request template
* Data conversion and binding for parameters of handler method
* Annotation-based config for handler mapping

## Implementation Model

This project is implemented based-on Spring MVC idea, with front-controller model. 
WebSocketDispatcherHandler has similar role to DispatcherServlet in Spring MVC. 
In addition, I also built a WebSocketHandlerMapping for mapping url to handler method, 
and a WebSocketHandlerAdapter to invoke the handler method for each specific request.

## Reference documentation

For further reference, please consider the following sections:

* [Netty 4.x user guide](https://netty.io/wiki/user-guide-for-4.x.html)
* [Java reflections support library dependency](https://mvnrepository.com/artifact/org.reflections/reflections/0.10.2)

