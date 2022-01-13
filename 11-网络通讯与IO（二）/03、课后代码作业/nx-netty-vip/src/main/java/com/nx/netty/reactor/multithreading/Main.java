package com.nx.netty.reactor.multithreading;

public class Main {
    public static void main(String[] args) {
        Reactor reactor = new Reactor(9100);
        reactor.run();
    }
}
