package com.nx.netty.reactor.single;

public class ServerClient {
    public static void main(String[] args) {
        Reactor reactor = new Reactor(9090);
        reactor.run();
    }
}


