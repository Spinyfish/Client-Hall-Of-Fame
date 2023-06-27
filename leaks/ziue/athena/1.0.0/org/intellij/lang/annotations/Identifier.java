package org.intellij.lang.annotations;

import java.lang.annotation.*;

@Pattern("\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*")
public @interface Identifier {
}
