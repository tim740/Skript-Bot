package uk.tim740.SkriptBot;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by tim740 on 26/12/2016
 */
class Redirect extends OutputStream {
    private TextArea output;

    Redirect(TextArea ta) {
        output = ta;
    }

    @Override
    public void write(int b) throws IOException {
        Platform.runLater(() -> output.appendText(String.valueOf((char) b)));
    }
}