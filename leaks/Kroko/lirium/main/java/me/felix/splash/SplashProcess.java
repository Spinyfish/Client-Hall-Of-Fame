package me.felix.splash;

import lombok.AllArgsConstructor;

public class SplashProcess {


    public Stage currentStage = new Stage("First Setup", 10);
    public void updateStage(String text, int process) {
        currentStage = new Stage(text, process);
    }

    @AllArgsConstructor
    public class Stage {
        public final String text;
        public final int process;
    }

}
