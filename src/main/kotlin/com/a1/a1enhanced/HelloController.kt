package com.a1.a1enhanced

import javafx.fxml.FXML
import javafx.scene.control.Label

class HelloController {
    @FXML
    private lateinit var welcomeText: Label

    @FXML
    private fun onHelloButtonClick() {
        welcomeText.text = "Oh hello there"
    }
}