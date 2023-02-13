package com.a1.a1enhanced

import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.scene.control.Label
import javafx.scene.control.Separator
import javafx.scene.layout.HBox
import kotlin.math.roundToInt

class StatusView(val model: Model): HBox(), IView {
    private var averageLabel = Label("Course average: ${model.getCourseAverage()}")
    private var takenLabel = Label("Courses taken: ${model.getCoursesTaken()}")
    private var failedLabel = Label("Courses Failed: ${model.getNumFailed()}")
    private var coursesWD = Label("Courses WD'ed: ${model.getNumCourses() - model.getNumNotWD()}")
    private var medianLabel = Label("Course Median: ${model.getCourseMedian()}")
    private var likedLabel = Label("Courses Liked: ${model.getLikedCourses()}")
    private val averageSeparator = Separator(Orientation.VERTICAL)
    private val coursesTakenSeparator = Separator(Orientation.VERTICAL)
    private val coursesFailedSeparator = Separator(Orientation.VERTICAL)
    private val coursesMedianSeparator = Separator(Orientation.VERTICAL)
    private val coursesLikedSeparator = Separator(Orientation.VERTICAL)


    init {
        this.style = "-fx-background-color: gainsboro"
        averageSeparator.padding = Insets(0.0, 2.0, 0.0, 2.0)
        coursesTakenSeparator.padding = Insets(0.0, 2.0, 0.0, 2.0)
        coursesFailedSeparator.padding = Insets(0.0, 2.0, 0.0, 2.0)
        coursesMedianSeparator.padding = Insets(0.0, 2.0, 0.0, 2.0)
        coursesLikedSeparator.padding = Insets(0.0, 2.0, 0.0, 2.0)
        coursesLikedSeparator.isVisible = false; // Only need to separate when showing WD'ed
        coursesWD.isVisible = false
        this.children.addAll(averageLabel, averageSeparator, takenLabel, coursesTakenSeparator, failedLabel,
            coursesFailedSeparator, medianLabel, coursesMedianSeparator, likedLabel, coursesLikedSeparator,
            coursesWD)
        this.padding = Insets(0.0, 0.0,0.0,5.0)
        model.addView(this)
    }

    override fun updateView() {
        var sum = 0
        var numNotWD = 0
        var numKeep = 0
        var avg = 0.0
        var numFailed = 0
        val filterType = model.getFilterType()
        for (c in model.getCourses()) {
            if (filterType == c.classType && !c.removed) {
                if (c.gradeValue != -1) {
                    sum += c.gradeValue
                    numNotWD++
                    if (c.gradeValue < 50) {
                        numFailed++
                    }
                }
                numKeep++
            }
        }
        if (filterType == 0) {
            avg = model.getCourseAverage()
            numKeep = model.getCoursesTaken()
            numNotWD = model.getNumNotWD()
            numFailed = model.getNumFailed()
        } else {
            if (numNotWD == 0) {
                avg = 0.0
            } else {
                avg = sum.toDouble()/numNotWD.toDouble()
            }
        }

        this.averageLabel.text = "Course average: ${(avg*100.00).roundToInt()/100.00}"
        this.takenLabel.text = "Courses taken: $numNotWD"
        this.failedLabel.text = "Courses Failed: $numFailed"
        if (model.getIncludingWd()) {
            this.coursesLikedSeparator.isVisible = true
            this.coursesWD.text = "Courses WD'ed: ${numKeep - numNotWD}"
            this.coursesWD.isVisible = true
        } else {
            this.coursesLikedSeparator.isVisible = false
            this.coursesWD.isVisible = false
        }
        this.medianLabel.text = "Course Median: ${model.getCourseMedian()}"
        model.setLikedCourses()
        this.likedLabel.text = "Courses liked: ${model.getLikedCourses()}"
    }
}