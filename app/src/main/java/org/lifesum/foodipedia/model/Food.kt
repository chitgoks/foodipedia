package org.lifesum.foodipedia.model

data class Food(
    var title: String = "",
    var calories: Int,
    var carbs: Double,
    var protein: Double,
    var fat: Double,
    var saturatedfat: Double,
    var unsaturatedfat: Double,
    var fiber: Double,
    var cholesterol: Double,
    var sugar: Double,
    var sodium: Double,
    var potassium: Double,
    var gramsperserving: Double,
    var pcstext: String = ""
) {
    constructor() : this("", 0, 0.0, 0.0, 0.0, 0.0,0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, "")
}