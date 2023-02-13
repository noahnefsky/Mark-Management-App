package com.a1.a1enhanced

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane
import javafx.stage.Stage


class HelloApplication : Application() {
    override fun start(stage: Stage) {
        val model = Model()

        val root = BorderPane()
        root.top = ToolbarView(model)
        root.bottom = StatusView(model)
        root.center = CoursesView(model)
        stage.apply {
            title = "Mark Management App"
            scene = Scene(root, 800.0, 600.0)
        }.show()
    }

}