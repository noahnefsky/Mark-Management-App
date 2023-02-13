package com.a1.a1enhanced

import javafx.collections.FXCollections
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.scene.control.*
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import kotlin.Exception

class ToolbarView(val model: Model): VBox(), IView {
    val sortLabel = Label("Sort by:")
    val filterLabel = Label("Filter by:")
    val includeWD = Label("Include WD:")
    val wdCheckBox = CheckBox()
    val sortChoiceBox = ChoiceBox( FXCollections.observableArrayList("Course Code", "Term", "Grade (Ascending)","Grade (Descending)"))
    val filterChoiceBox = ChoiceBox( FXCollections.observableArrayList("All Courses", "CS Courses Only", "Math Courses Only", "Other"))
    val sortSeparator = Separator(Orientation.VERTICAL)
    val filterSeparator = Separator(Orientation.VERTICAL)
    val likedCheckBox = CheckBox()
    val likedLabel = Label("Liked:")

    val courseTextField = TextField("")
    val courseDescriptionTextField = TextField("")
    val termDrop = ChoiceBox( FXCollections.observableArrayList("F20", "W21", "S21", "F21", "W22", "S22", "F22", "W23", "S23", "F23"))
    val gradeTextField = TextField("") // MAKE SURE IT HAS TO BE IN A SPECIFIC RANGE!!!
    val createButton = Button("Create")

    val toolBar1 = ToolBar()
    val toolBar2 = ToolBar()

    override fun updateView() {
        courseTextField.text = ""
        courseDescriptionTextField.text = ""
        termDrop.value = ""
        gradeTextField.text = ""
    }




    init {
        filterChoiceBox.value = "All Courses"
        createButton.onAction = EventHandler {
            try {
                if (termDrop.value.length < 3) {
                    throw Exception("Invalid Term: " + termDrop.value)
                }
                // termOrder is the year for the term + 1 if winter, 2 if summer and 3 if fall
                // --> so we can sort by term
                var termOrder = termDrop.value.substring(1).toInt() * 10
                when (termDrop.value[0]) {
                    'W' -> termOrder += 1
                    'S' -> termOrder += 2
                    'F' -> termOrder += 3
                }
                var gradeValue = 0
                if (gradeTextField.text == "WD" || gradeTextField.text == "wd" || gradeTextField.text == "Wd") {
                    gradeValue = -1
                } else {
                    gradeValue = gradeTextField.text.toInt()
                }
                // Keep track of class type for filter
                // 0 = other, 1 = CS, 2 = math courses
                var classType = 1
                if (courseTextField.text.length >= 2 && (courseTextField.text.substring(0, 2) == "CS" ||
                            courseTextField.text.substring(0, 2) == "cs")) {
                    classType = 2
                    if (courseTextField.text.length >= 3 && !courseTextField.text[2].isDigit()) {
                        classType = 1
                    }
                } else if (courseTextField.text.length >= 2 && (courseTextField.text.substring(0, 2) == "CO" ||
                            courseTextField.text.substring(0, 2) == "co")) {
                    classType = 3
                    if (courseTextField.text.length >= 3 && !courseTextField.text[2].isDigit()) {
                        classType = 1
                    }
                } else if (courseTextField.text.length >= 4 && (courseTextField.text.substring(0, 4) == "MATH" ||
                            courseTextField.text.substring(0, 4) == "math" ||
                    courseTextField.text.substring(0, 4) == "STAT" || courseTextField.text.substring(0, 4) == "stat")) {
                    classType = 3
                    if (courseTextField.text.length >= 5 && !courseTextField.text[4].isDigit()) {
                        classType = 1
                    }
                }

                var course = Model.Course(
                    courseTextField.text,
                    courseDescriptionTextField.text,
                    termDrop.value,
                    gradeTextField.text,
                    model.getNumCourses(),
                    false,
                    termOrder,
                    gradeValue,
                    classType,
                    likedCheckBox.isSelected
                )

                model.addCourse(course)
            } catch (e: Exception) {
                // so don't crash if invalid input
            }
        }

        wdCheckBox.selectedProperty().addListener { _, _, newValue ->
            if (newValue.not()) {
                model.setIncludingWd(false)
            } else {
                model.setIncludingWd(true)
            }
        }

        sortChoiceBox.selectionModel.selectedItemProperty().addListener { _, _, _ ->
            when(sortChoiceBox.value) {
                "Grade (Ascending)" -> model.sortByGradeAsc()
                "Grade (Descending)" -> model.sortByGradeDes()
                "Term" -> model.sortByTerm()
                "Course Code" -> model.sortByCode()
            }
        }

        filterChoiceBox.selectionModel.selectedItemProperty().addListener { _, _, _ ->
            when(filterChoiceBox.value) {
                "All Courses" -> model.setFilterType(0)
                "CS Courses Only" -> model.setFilterType(2)
                "Math Courses Only" -> model.setFilterType(3)
                "Other" -> model.setFilterType(1)
            }
        }

        courseDescriptionTextField.style = "-fx-background-color: white"
        courseTextField.style = "-fx-background-color: white"
        courseTextField.minWidth = 100.00
        courseTextField.maxWidth = 100.00
        gradeTextField.minWidth = 55.00
        gradeTextField.maxWidth = 55.00
        gradeTextField.style = "-fx-background-color: white"
        toolBar1.items.addAll(sortLabel, sortChoiceBox, sortSeparator, filterLabel, filterChoiceBox, filterSeparator, includeWD, wdCheckBox)//, spacer, spacer2)
        toolBar2.items.addAll(courseTextField, courseDescriptionTextField, termDrop, gradeTextField, likedLabel, likedCheckBox, createButton)
        toolBar2.style = "-fx-background-color: gainsboro; -fx-background-radius: 10 10 10 10;"
        var toolBar2Holder = HBox()
        toolBar2Holder.children.add(toolBar2)
        toolBar2Holder.padding = Insets(7.5)

        HBox.setHgrow(courseDescriptionTextField, Priority.ALWAYS)
        toolBar2.prefWidthProperty().bind(toolBar2Holder.widthProperty())
        toolBar2Holder.prefWidthProperty().bind(this.widthProperty())
        this.children.addAll(toolBar1, toolBar2Holder)
        model.addView(this)
    }

}