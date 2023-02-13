package com.a1.a1enhanced

import javafx.collections.FXCollections
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.scene.control.*
import javafx.scene.layout.*


class CoursesView(val model: Model): ScrollPane(), IView {
    private var vBoxForList = VBox()
    init {
        this.padding = Insets(10.0, 0.0, 0.0, 0.0)
        this.content = vBoxForList
        model.addView(this)
    }


    override fun updateView() {
        var courses = model.getCourses()

        if (courses.isEmpty()){
            return
        }
        // Sort the courses
        // This is needed here if we have a sort on and a course is added
        when(model.getSortType()) {
            1 -> courses.sortBy { it.gradeValue }
            2 -> courses.sortByDescending { it.gradeValue }
            3 -> courses.sortBy { it.termOrder }
            4 -> courses.sortWith(compareBy(String.CASE_INSENSITIVE_ORDER, { it.code }))
        }

        // Will copy newVBox into this.vBox
        val newVBox = VBox()
        for (course in courses) {
            var filterOut = false
            val filterType = model.getFilterType()

            // Filter here (we are just filtering what we, we are not changing the list
            if (filterType == 2) {
                if (course.code.length < 2 || (course.code.substring(0, 2) != "CS" && course.code.substring(0, 2) != "cs")) {
                    filterOut = true
                }
            } else if (filterType == 3) {
                if (course.code.length >= 4 && (course.code.substring(0, 4) == "MATH" || course.code.substring(0, 4) == "math")) {
                    filterOut = false
                } else if (course.code.length >= 4 && (course.code.substring(0, 4) == "STAT" || course.code.substring(0, 4) == "stat")) {
                    filterOut = false
                } else filterOut =
                    !(course.code.length >= 2 && (course.code.substring(0, 2) == "CO" || course.code.substring(0, 2) == "co"))
            } else if (filterType == 4) {
                if ((course.code.length >= 2 && (course.code.substring(0, 2) == "CS" || course.code.substring(0, 2) == "cs" ||
                            course.code.substring(0, 2) == "CO" || course.code.substring(0, 2) == "co")) ||
                    (course.code.length >= 4 && (course.code.substring(0, 4) == "MATH" || course.code.substring(0, 4) == "math" ||
                            course.code.substring(0, 4) == "STAT" || course.code.substring(0, 4) == "stat"))) {
                    filterOut = true
                }
            }

            // Regular course setup
            val courseText = TextField(course.code)
            val courseDescription = TextField(course.name)
            val updateButton = Button("Update")
            val likedLabel = Label("Liked:")
            var likedCheckBox = CheckBox()
            likedCheckBox.isSelected = course.liked
            val termDrop = ChoiceBox(
                FXCollections.observableArrayList(
                    "F20",
                    "W21",
                    "S21",
                    "F21",
                    "W22",
                    "S22",
                    "F22",
                    "W23",
                    "S23",
                    "F23"
                )
            )
            val deleteButton = Button("Delete")

            termDrop.value = course.term
            termDrop.selectionModel.selectedItemProperty().addListener { _, _, _ ->
                updateButton.isDisable = false;
                deleteButton.text = "Undo"
            }

            updateButton.isDisable = true;

            courseText.minWidth = 100.00
            courseText.maxWidth = 100.00
            courseText.isEditable = false

            val gradeText = TextField(course.grade)
            gradeText.minWidth = 55.00
            gradeText.maxWidth = 55.00

            // Try catches for bad inputs --> just do nothing
            // We add listners for textfields and onaction events for buttons

            courseDescription.textProperty().addListener{ _, _, _ ->
                try {
                    updateButton.isDisable = false;
                    deleteButton.text = "Undo"
                } catch (e: Exception) {
                    updateButton.isDisable = true;
                }
            }

            gradeText.textProperty().addListener { _, _, _ ->
                try {
                    if (gradeText.text == "WD" || gradeText.text.toInt() in 0..100) {
                        updateButton.isDisable = false;
                    }
                    deleteButton.text = "Undo"
                } catch (e: Exception) {
                    updateButton.isDisable = true;
                }
            }

            likedCheckBox.selectedProperty().addListener { _, _, _ ->
                updateButton.isDisable = false;
                deleteButton.text = "Undo"
            }

            updateButton.onAction = EventHandler {
                model.setLikedCourses()
                var termOrder = termDrop.value.substring(1).toInt() * 10
                when (termDrop.value[0]) {
                    'W' -> termOrder += 1
                    'S' -> termOrder += 2
                    'F' -> termOrder += 3
                }

                // Keep track of class type for filter
                // 1 = other, 2 = CS, 3 = math courses
                var classType = 1
                if (courseText.text.length >= 2 && (courseText.text.substring(0, 2) == "CS" || courseText.text.substring(0, 2) == "cs")) {
                    classType = 2
                    if (courseText.text.length >= 3 && !courseText.text[2].isDigit()) {
                        classType = 1
                    }
                } else if (courseText.text.length >= 2 && (courseText.text.substring(0, 2) == "CO" || courseText.text.substring(0, 2) == "co")) {
                    classType = 3
                    if (courseText.text.length >= 3 && !courseText.text[2].isDigit()) {
                        classType = 1
                    }
                } else if (courseText.text.length >= 4 && (courseText.text.substring(0, 4) == "MATH" || courseText.text.substring(0, 4) == "math"
                    || courseText.text.substring(0, 4) == "STAT" || courseText.text.substring(0, 4) == "stat")) {
                    classType = 3
                    if (courseText.text.length >= 5 && !courseText.text[4].isDigit()) {
                        classType = 1
                    }

                }

                var gradeValue = 0 // So we can sort properly
                try {
                    if (gradeText.text == "WD" || gradeText.text == "wd" || gradeText.text == "Wd") {
                        gradeValue = -1
                    } else {
                        gradeValue = gradeText.text.toInt()
                    }
                    val nc = Model.Course(
                        courseText.text,
                        courseDescription.text,
                        termDrop.value,
                        gradeText.text,
                        course.num,
                        course.removed,
                        termOrder,
                        gradeValue,
                        classType,
                        likedCheckBox.isSelected()
                    )
                    model.updateCourse(nc)
                } catch (e: Exception) {
                    updateButton.isDisable = true;
                }
            }

            deleteButton.onAction = EventHandler {
                if (deleteButton.text == "Undo") {
                    deleteButton.text == "Delete"
                    gradeText.text = courses[course.num].grade
                    courseDescription.text = courses[course.num].name
                    termDrop.value = courses[course.num].term
                    likedCheckBox.isSelected = courses[course.num].liked
                } else {
                    model.deleteCourse(course.num)
                }
            }


            val hBox = HBox(courseText, courseDescription, termDrop, gradeText, likedLabel, likedCheckBox, updateButton, deleteButton)
            hBox.prefWidth = 800.00

            // Set course colour
            if (course.grade == "WD") {
                hBox.style = "-fx-background-color: DARKSLATEGRAY;"
            } else {
                when (course.grade.toInt()) {
                    in 0..49 -> hBox.style = "-fx-background-color: LIGHTCORAL;"
                    in 50..59 -> hBox.style = "-fx-background-color: LIGHTBLUE;"
                    in 60..90 -> hBox.style = "-fx-background-color: LIGHTGREEN;"
                    in 91..95 -> hBox.style = "-fx-background-color: SILVER;"
                    in 96..100 -> hBox.style = "-fx-background-color: GOLD;"
                    else -> return
                }
            }
            hBox.style += "-fx-background-radius: 10 10 10 10;"
            hBox.padding = Insets(7.5)
            // Holder so we have proper spacing
            var hBoxHolder = HBox()
            hBoxHolder.padding = Insets(0.0, 7.5, 7.5, 7.5)

            if (course.classType != filterType && filterType != 0) {
                filterOut = true
            }

            // If we have deleted the class or are currently filtering out, then don't show it
            if (!course.removed && !filterOut) {
                if (course.grade == "WD" && model.getIncludingWd()) {
                    hBoxHolder.children.add(hBox)
                    newVBox.children.add(hBoxHolder)
                } else if (course.grade != "WD") {
                    hBoxHolder.children.add(hBox)
                    newVBox.children.add(hBoxHolder)
                }
            }
            HBox.setHgrow(courseDescription, Priority.ALWAYS)
            HBox.setHgrow(hBox, Priority.ALWAYS)
            hBoxHolder.prefWidthProperty().bind(this.vBoxForList.widthProperty())
            VBox.setVgrow(hBox, Priority.ALWAYS)
        }


        vBoxForList.prefWidthProperty().bind(this.widthProperty())
        this.vBoxForList.children.clear()
        this.vBoxForList.children.addAll(newVBox.children)
    }

}