/*
 * Copyright Felix Hans from AccountStorage coded for haze.yt / Lirium . - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited
 */

package me.felix.altmanager.data;

import me.felix.altmanager.group.AccountGroup;

import java.util.HashMap;
import java.util.UUID;

public class AccountStorage {
    public static final HashMap<Account, UUID> ACCOUNT_UUID_HASH_MAP = new HashMap<>();

    public static void addAccount(final String email, final String password, final UUID UUID, final Account.Type type, AccountGroup... group) {
        final Account account;
        if (group[0] != null)
            account = new Account(type).setupEmail(email).setupPassword(password).setupUUID(UUID).moveToGroup(group[0]);
        else
            account = new Account(type).setupEmail(email).setupPassword(password).setupUUID(UUID);
        if (!ACCOUNT_UUID_HASH_MAP.containsValue(UUID))
            ACCOUNT_UUID_HASH_MAP.put(account, account.getUuid());
        else
            System.out.println("Account already existing!");
    }

}
