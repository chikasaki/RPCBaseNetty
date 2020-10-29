package rpc.client;

import rpc.message.CallContent;

import java.util.concurrent.CompletableFuture;

public interface Transportation {
    CompletableFuture transport(CallContent content);
}
