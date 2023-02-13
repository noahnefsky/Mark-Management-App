package com.a1.a1enhanced

import kotlin.contracts.contract

class Model {
    class Course(var code: String, var name: String, var term: String, var grade: String, var num: Int,
                 var removed:Boolean, var termOrder: Int, var gradeValue: Int, var classType: Int, var liked: Boolean)

    private val views: ArrayList<IView> = ArrayList()

    private var likedCourses = 0
    private var sortType = 0
    private var filterType = 0
    private var includingWD = false
    private var numCourses = 0
    private var coursesTaken = 0
    private var numNotWD = 0
    private var courseAverage: Double = 0.00
    private var sumCourses = 0
    private var numFailed = 0
    private var courses = mutableListOf<Course>()

    fun getFilterType(): Int {
        return filterType
    }

    fun setFilterType(type: Int) {
        filterType = type
        notifyObservers()
    }

    fun getSortType(): Int {
        return sortType
    }

    fun addView(view: IView) {
        views.add(view)
        view.updateView()
    }

    fun removeView(view: IView?) {
        views.remove(view)
    }

    fun notifyObservers() {
        for (view in views) {
            view.updateView()
        }
    }

    fun getIncludingWd(): Boolean {
        return includingWD
    }

    fun setIncludingWd(include: Boolean) {
        includingWD = include
        notifyObservers()
    }

    fun getCoursesTaken(): Int {
        return coursesTaken
    }

    fun getNumNotWD(): Int {
        return numNotWD
    }
    fun getNumCourses(): Int {
        return numCourses
    }


    fun getCourseAverage(): Double {
        return Math.round(courseAverage * 100.0)/100.0
    }

    fun getCourseMedian(): Double {
        if (courses.isEmpty()) {
            return 0.0
        }
        val coursesCopy = mutableListOf<Int>()
        courses.forEach {
            if (it.gradeValue != -1 && (filterType == 0 || filterType == it.classType) && !it.removed) {
                coursesCopy.add(it.gradeValue)
            }
        }
        if (coursesCopy.isEmpty()) {
            return 0.0
        }
        coursesCopy.sort()
        val len = coursesCopy.size
        if (len % 2 == 0) {
            return (coursesCopy[len/2].toDouble() + coursesCopy[len/2 - 1].toDouble())/2.0
        } else {
            return coursesCopy[len/2].toDouble()
        }

    }


    fun getNumFailed(): Int {
        return numFailed
    }


    fun addCourse(c: Course) {
        try {
            if (((c.grade != "WD") && (c.grade.toInt() < 0 || c.grade.toInt() > 100))
                || c.term.isEmpty()
            ) {
                return
            }
            courses.add(c)
            numCourses++
            coursesTaken++
            if (c.grade != "WD") {
                numNotWD++
                sumCourses += c.grade.toInt()
                courseAverage = sumCourses.toDouble() / numNotWD.toDouble()
                if (c.grade.toInt() < 50) {
                    numFailed++
                }
            }
            if (c.liked) {
                likedCourses++
            }
            notifyObservers()
        } catch (e: Exception) {
            return
        }
    }
    fun getCourses(): MutableList<Course> {
        return courses
    }

    fun updateCourse(nc: Course) {
        for (c in courses) {
            if (nc.num == c.num) {
                if (((nc.grade != "WD") && (nc.grade.toInt() < 0 || nc.grade.toInt() > 100))
                    || nc.term.isEmpty()) {
                    return
                }
                if (nc.grade == "WD" && c.grade != "WD") {
                    if (c.grade.toInt() < 50) {
                        numFailed--
                    }
                    numNotWD--
                    sumCourses -= c.grade.toInt()
                    courseAverage = sumCourses.toDouble() / numNotWD.toDouble()
                } else if (c.grade == "WD" && nc.grade != "WD") {
                    if (nc.grade.toInt() < 50) {
                        numFailed++
                    }
                    numNotWD++
                    sumCourses += nc.grade.toInt()
                    courseAverage = sumCourses.toDouble() / numNotWD.toDouble()
                } else if (c.grade != nc.grade) {
                    if (nc.grade.toInt() < 50 && c.grade.toInt() >= 50) {
                        numFailed++
                    } else if (nc.grade.toInt() >= 50 && c.grade.toInt() < 50) {
                        numFailed--
                    }
                    sumCourses -= c.grade.toInt()
                    sumCourses += nc.grade.toInt()
                    courseAverage = sumCourses.toDouble() / numNotWD.toDouble()
                }
                c.grade = nc.grade
                c.termOrder = nc.termOrder
                c.term = nc.term
                c.name = nc.name
                c.gradeValue = nc.gradeValue
                if (c.liked && !nc.liked) {
                    likedCourses--
                } else if (!c.liked && nc.liked) {
                    likedCourses++
                }
                c.liked = nc.liked
                break
            }
        }
        notifyObservers()
    }

    fun deleteCourse(num: Int) {
        for (c in courses) {
            if (c.num == num) {
                c.removed = true
                if (c.grade != "WD") {
                    sumCourses -= c.grade.toInt()
                    numNotWD--
                    courseAverage = sumCourses.toDouble() / numNotWD.toDouble()
                    if (c.grade.toInt() < 50) {
                        numFailed--
                    }
                }
                if (c.liked) {
                    likedCourses--
                }
                coursesTaken--
            }
        }
        notifyObservers()
    }

    fun sortByGradeAsc() {
        sortType = 1
        courses.sortBy { it.gradeValue }
        notifyObservers()
    }

    fun sortByGradeDes() {
        sortType = 2
        courses.sortByDescending { it.gradeValue }
        notifyObservers()
    }

    fun sortByTerm() {
        sortType = 3
        courses.sortBy { it.termOrder }
        notifyObservers()
    }

    fun sortByCode() {
        sortType = 4
        courses.sortWith(compareBy(String.CASE_INSENSITIVE_ORDER, { it.code }))
        notifyObservers()
    }

    fun getLikedCourses(): Int {
        return likedCourses
    }

    fun setLikedCourses() {
        var numLiked = 0
        courses.forEach{
            if (it.liked && (filterType == 0 || filterType == it.classType) && !it.removed) {
                if (!(it.gradeValue == -1 && !includingWD)) {
                    numLiked++
                }
            }
        }
        likedCourses = numLiked
    }
}