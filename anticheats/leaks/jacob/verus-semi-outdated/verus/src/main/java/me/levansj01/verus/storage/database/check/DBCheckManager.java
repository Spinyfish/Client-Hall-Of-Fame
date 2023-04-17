package me.levansj01.verus.storage.database.check;

import me.levansj01.verus.check.manager.*;
import me.levansj01.verus.check.*;
import me.levansj01.verus.storage.*;
import java.util.function.*;
import java.util.*;

public class DBCheckManager extends CheckManager
{

    
    @Override
    public void setEnabled(final Check check, final boolean b) {
        this.setEnabled(check.identifier(), b);
    }
    
    @Override
    public void loadChecks() {
        StorageEngine.getInstance().getDatabase().loadCheckValues(this.checks, (Consumer)this::lambda.loadChecks.0);
    }
    
    private void lambda.loadChecks.0(final List list) {
        for (final CheckValues checkValues : list) {
            this.values.put(checkValues.getCheckId(), checkValues);
       
        }
    }
    
    @Override
    public void setMaxViolation(final String s, final int n) {
        super.setMaxViolation(s, n);
        StorageEngine.getInstance().getDatabase().updateCheckViolation(s, n);
    }
    
    @Override
    public void setAutoban(final Check check, final boolean b) {
        this.setAutoban(check.identifier(), b);
    }
    
    private void updateCommands(final CheckValues checkValues) {
    }
    
    @Override
    public void removeCommand(final CheckValues checkValues, final int n) {
    }
    
    @Deprecated
    @Override
    public void saveChecks() {
    }
    
    @Override
    public void setEnabled(final String s, final boolean b) {
        super.setEnabled(s, b);
        StorageEngine.getInstance().getDatabase().updateCheckAlerts(s, b);
    }
    
    @Override
    public void addCommand(final CheckValues checkValues, final String s) {
    }
    
    @Override
    public void setAutoban(final String s, final boolean b) {
        super.setAutoban(s, b);
        StorageEngine.getInstance().getDatabase().updateCheckPunish(s, b);
    }
}
