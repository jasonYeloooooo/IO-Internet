package com.nx.netty.reactor.masterSlave;

public class Main {
    public static void main(String[] args) {
        Reactor reactor = new Reactor(9090);
        reactor.run();
    }
}
