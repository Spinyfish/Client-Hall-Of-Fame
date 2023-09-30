/*
 * Decompiled with CFR 0.152.
 */
package com.thealtening.api.retriever;

import com.thealtening.api.TheAlteningException;
import com.thealtening.api.response.Account;
import com.thealtening.api.response.License;
import com.thealtening.api.retriever.BasicDataRetriever;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class AsynchronousDataRetriever
extends BasicDataRetriever {
    public AsynchronousDataRetriever(String apiKey) {
        super(apiKey);
    }

    public CompletableFuture<License> getLicenseDataAsync() {
        return this.completeTask(BasicDataRetriever::getLicense);
    }

    public CompletableFuture<Account> getAccountDataAsync() {
        return this.completeTask(BasicDataRetriever::getAccount);
    }

    public CompletableFuture<Boolean> isPrivateAsync(String token) {
        return this.completeTask(dr -> dr.isPrivate(token));
    }

    public CompletableFuture<Boolean> isFavoriteAsync(String token) {
        return this.completeTask(dr -> dr.isFavorite(token));
    }

    public CompletableFuture<List<Account>> getPrivatedAccountsAsync() {
        return this.completeTask(BasicDataRetriever::getPrivatedAccounts);
    }

    public CompletableFuture<List<Account>> getFavoritedAccountsAsync() {
        return this.completeTask(BasicDataRetriever::getFavoriteAccounts);
    }

    private <T> CompletableFuture<T> completeTask(Function<BasicDataRetriever, T> function) {
        CompletableFuture<T> returnValue = new CompletableFuture<T>();
        try {
            returnValue.complete(function.apply(this));
        }
        catch (TheAlteningException exception) {
            returnValue.completeExceptionally(exception);
        }
        return returnValue;
    }
}

