package dev.africa.pandaware.impl.script.functions;


import dev.africa.pandaware.utils.client.Printer;

public class ScriptPrinterFunctions {

    public void chat(Object text) {
        Printer.chat(text);
    }

    public void console(Object text) {
        Printer.consoleInfo(text);
    }
}
