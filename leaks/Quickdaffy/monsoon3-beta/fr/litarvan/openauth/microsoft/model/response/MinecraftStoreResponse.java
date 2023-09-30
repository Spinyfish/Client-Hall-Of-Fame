/*
 * Decompiled with CFR 0.152.
 */
package fr.litarvan.openauth.microsoft.model.response;

public class MinecraftStoreResponse {
    private final StoreProduct[] items;
    private final String signature;
    private final String keyId;

    public MinecraftStoreResponse(StoreProduct[] items, String signature, String keyId) {
        this.items = items;
        this.signature = signature;
        this.keyId = keyId;
    }

    public StoreProduct[] getItems() {
        return this.items;
    }

    public String getSignature() {
        return this.signature;
    }

    public String getKeyId() {
        return this.keyId;
    }

    public static class StoreProduct {
        private final String name;
        private final String signature;

        public StoreProduct(String name, String signature) {
            this.name = name;
            this.signature = signature;
        }

        public String getName() {
            return this.name;
        }

        public String getSignature() {
            return this.signature;
        }
    }
}

